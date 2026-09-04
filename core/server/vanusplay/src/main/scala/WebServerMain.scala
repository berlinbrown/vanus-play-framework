/**
 * Vanus Play Framework Web Server Main Class
 * Berlin Brown - 2026
 */
import java.io.File
import java.util.{HashMap, List, Map}

import org.nanohttpd.protocols.http.{IHTTPSession, NanoHTTPD}
import org.nanohttpd.protocols.http.request.Method
import org.nanohttpd.protocols.http.response.{IStatus, Response, Status}
import org.nanohttpd.util.ServerRunner
import org.nanohttpd.webserver.{InternalRewrite, WebServerPlugin}

import scala.jdk.CollectionConverters.*

object WebServerMain:
  val indexFileNames: List[String] = new java.util.ArrayList[String]()
  val mimeTypeHandlers: Map[String, WebServerPlugin] = new java.util.HashMap[String, WebServerPlugin]()

  indexFileNames.add(VanusConstants.DefaultIndexHtml)
  indexFileNames.add(VanusConstants.DefaultIndexHtm)

  VanusRoutes.addRoute(VanusRoutes.systemInfoPath, new GeneralHandler())

  def main(args: Array[String]): Unit =
    val config = VanusServerConfig.fromArgs(args)
    VanusPlugins.registerAvailablePlugins(mimeTypeHandlers, indexFileNames, config.options.asJava)
    ServerRunner.executeInstance(new WebServer(config.host, config.port, config.rootDirs.asJava, config.quiet, config.cors.orNull))

class WebServer(host: String, port: Int, wwwroots: List[File], quiet: Boolean, cors: String)
    extends NanoHTTPD(host, port):

  private val rootDirs = wwwroots.asScala.toVector
  private val corsOption = Option(cors)

  override def serve(session: IHTTPSession): Response =
    logRequest(session)
    rootDirs.find(!_.isDirectory) match
      case Some(root) => internalError(s"given path is not a directory ($root).")
      case None => respond(session.getHeaders, session, session.getUri)

  private def respond(headers: Map[String, String], session: IHTTPSession, uri: String): Response =
    val response =
      if corsOption.isDefined && Method.OPTIONS == session.getMethod then
        Response.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, null, 0)
      else
        defaultRespond(headers, session, uri)
    VanusCors(response, corsOption)

  private def defaultRespond(headers: Map[String, String], session: IHTTPSession, originalUri: String): Response =
    val uri = normalizeUri(originalUri)
    VanusRoutes.find(uri) match
      case Some(handler) => handler.get(session)
      case None => respondToFileRequest(headers, session, uri)

  private def respondToFileRequest(headers: Map[String, String], session: IHTTPSession, uri: String): Response =
    if uri.contains("../") then return forbidden(VanusConstants.ErrorForbiddenTraversal)

    rootDirs.find(root => canServeUri(uri, root)) match
      case None => notFound
      case Some(root) => respondFromRoot(headers, session, uri, root)

  private def respondFromRoot(headers: Map[String, String], session: IHTTPSession, uri: String, homeDir: File): Response =
    val file = new File(homeDir, uri)

    if file.isDirectory && !uri.endsWith("/") then
      redirectTo(uri + "/")
    else if file.isDirectory then
      respondFromDirectory(headers, session, uri, file)
    else
      respondFromFile(headers, session, uri, homeDir, file)

  private def respondFromDirectory(headers: Map[String, String], session: IHTTPSession, uri: String, file: File): Response =
    val indexFile = VanusDirectory.findIndexFileInDirectory(file, WebServerMain.indexFileNames)
    if indexFile == null then
      if file.canRead then VanusFileResponses.fixedResponse(Status.OK, NanoHTTPD.MIME_HTML, 
            VanusDirectory.listDirectory(uri, file))
      else forbidden(VanusConstants.ErrorNoDirectoryListing)
    else
      respond(headers, session, uri + indexFile)

  private def respondFromFile(headers: Map[String, String], session: IHTTPSession, uri: String, homeDir: File, file: File): Response =
    val mime = NanoHTTPD.getMimeTypeForFile(uri)
    val plugin = WebServerMain.mimeTypeHandlers.get(mime)

    if plugin != null && plugin.canServeUri(uri, homeDir) then
      plugin.serveFile(uri, headers, session, file, mime) match
        case rewrite: InternalRewrite => respond(rewrite.getHeaders, session, rewrite.getUri)
        case null => notFound
        case response => response
    else
      Option(VanusFileResponses.serveFile(headers, file, mime)).getOrElse(notFound)

  private def canServeUri(uri: String, homeDir: File): Boolean =
    val file = new File(homeDir, uri)
    file.exists || Option(WebServerMain.mimeTypeHandlers.get(NanoHTTPD.getMimeTypeForFile(uri))).exists(_.canServeUri(uri, homeDir))

  private def normalizeUri(originalUri: String): String =
    val withoutQuery = originalUri.indexOf('?') match
      case -1 => originalUri
      case index => originalUri.substring(0, index)
    withoutQuery.trim.replace(File.separatorChar, '/')

  private def logRequest(session: IHTTPSession): Unit =
    if quiet then return
    println(session.getMethod.toString + " '" + session.getUri + "' ")
    session.getHeaders.asScala.foreach { case (name, value) =>
      println(s"  HDR: '$name' = '$value'")
    }
    session.getParms.asScala.foreach { case (name, value) =>
      println(s"  PRM: '$name' = '$value'")
    }

  private def redirectTo(uri: String): Response =
    val response = VanusFileResponses.fixedResponse(Status.REDIRECT, NanoHTTPD.MIME_HTML,
      "<html><body>Redirected: <a href=\"" + uri + "\">" + uri + "</a></body></html>")
    response.addHeader(VanusConstants.HeaderLocation, uri)
    response

  private def forbidden(message: String): Response =
    Response.newFixedLengthResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, VanusConstants.ErrorForbiddenPrefix + message)

  private def internalError(message: String): Response =
    Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, VanusConstants.ErrorInternalPrefix + message)

  private def notFound: Response =
    Response.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, VanusConstants.ErrorNotFound)
