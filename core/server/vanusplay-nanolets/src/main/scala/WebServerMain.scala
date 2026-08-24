import java.io.{File, FileInputStream, FilenameFilter, IOException}
import java.net.URLEncoder
import java.util.{ArrayList, Arrays, Collections, HashMap, List, Map, ServiceLoader, StringTokenizer}

import org.nanohttpd.protocols.http.{IHTTPSession, NanoHTTPD}
import org.nanohttpd.protocols.http.request.Method
import org.nanohttpd.protocols.http.response.{IStatus, Response, Status}
import org.nanohttpd.util.ServerRunner
import org.nanohttpd.webserver.{InternalRewrite, WebServerPlugin, WebServerPluginInfo}
import scala.jdk.CollectionConverters.*

object WebServerMain:
  val indexFileNames: List[String] = new ArrayList[String]()
  indexFileNames.add("index.html")
  indexFileNames.add("index.htm")
  val mimeTypeHandlers: Map[String, WebServerPlugin] = new HashMap[String, WebServerPlugin]()

  def main(args: Array[String]): Unit =
    val port = optionValue(args, "-p", "--port").map(_.toInt).getOrElse(8080)
    val host = optionValue(args, "-h", "--host").orNull
    val quiet = args.exists(arg => arg.equalsIgnoreCase("-q") || arg.equalsIgnoreCase("--quiet"))
    val cors = args.find(_.startsWith("--cors")).map { arg =>
      val equalIndex = arg.indexOf('=')
      if equalIndex > 0 then arg.substring(equalIndex + 1) else "*"
    }.orNull
    val rootDirs = new ArrayList[File]()
    args.sliding(2).foreach {
      case pair if pair(0).equalsIgnoreCase("-d") || pair(0).equalsIgnoreCase("--dir") =>
        rootDirs.add(new File(pair(1)).getAbsoluteFile)
      case _ => ()
    }
    if rootDirs.isEmpty then rootDirs.add(new File(".").getAbsoluteFile)
    val options = new HashMap[String, String]()
    options.put("host", host)
    options.put("port", port.toString)
    options.put("quiet", quiet.toString)
    options.put("home", rootDirs.asScala.map(_.getCanonicalPath).mkString(":"))
    val plugins = ServiceLoader.load(classOf[WebServerPluginInfo]).iterator()
    while plugins.hasNext do
      val info = plugins.next()
      info.getMimeTypes.foreach { mime =>
        registerPluginForMimeType(info.getIndexFilesForMimeType(mime), mime, info.getWebServerPlugin(mime), options)
      }
    ServerRunner.executeInstance(new WebServer(host, port, rootDirs, quiet, cors))

  private def optionValue(args: Array[String], shortName: String, longName: String): Option[String] =
    args.sliding(2).collectFirst {
      case pair if pair(0).equalsIgnoreCase(shortName) || pair(0).equalsIgnoreCase(longName) => pair(1)
    }

  private def registerPluginForMimeType(indexFiles: Array[String], mimeType: String, plugin: WebServerPlugin,
      options: Map[String, String]): Unit =
    if mimeType == null || plugin == null then return
    if indexFiles != null then
      indexFiles.foreach { filename =>
        val dot = filename.lastIndexOf('.')
        if dot >= 0 then NanoHTTPD.mimeTypes().put(filename.substring(dot + 1).toLowerCase, mimeType)
      }
      indexFileNames.addAll(Arrays.asList(indexFiles*))
    mimeTypeHandlers.put(mimeType, plugin)
    plugin.initialize(options)

class WebServer(host: String, port: Int, wwwroots: List[File], quiet: Boolean, cors: String)
    extends NanoHTTPD(host, port):

  private val rootDirs = new ArrayList[File](wwwroots)

  private def canServeUri(uri: String, homeDir: File): Boolean =
    val file = new File(homeDir, uri)
    file.exists || Option(WebServerMain.mimeTypeHandlers.get(NanoHTTPD.getMimeTypeForFile(uri))).exists(_.canServeUri(uri, homeDir))

  private def encodeUri(uri: String): String =
    val encoded = new StringBuilder
    val tokens = new StringTokenizer(uri, "/ ", true)
    while tokens.hasMoreTokens do
      val token = tokens.nextToken()
      if token == "/" then encoded.append('/')
      else if token == " " then encoded.append("%20")
      else encoded.append(URLEncoder.encode(token, "UTF-8"))
    encoded.toString

  private def findIndexFileInDirectory(directory: File): String =
    val files = WebServerMain.indexFileNames.iterator()
    while files.hasNext do
      val fileName = files.next()
      if new File(directory, fileName).isFile then return fileName
    null

  protected def getForbiddenResponse(message: String): Response =
    Response.newFixedLengthResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + message)

  protected def getInternalErrorResponse(message: String): Response =
    Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + message)

  protected def getNotFoundResponse: Response =
    Response.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.")

  private def fixedResponse(status: IStatus, mime: String, message: String): Response =
    val response = Response.newFixedLengthResponse(status, mime, message)
    response.addHeader("Accept-Ranges", "bytes")
    response

  private def respond(headers: Map[String, String], session: IHTTPSession, uri: String): Response =
    val response = if cors != null && Method.OPTIONS == session.getMethod then
      Response.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, null, 0)
    else defaultRespond(headers, session, uri)
    if cors != null then addCORSHeaders(response) else response

  private def defaultRespond(headers: Map[String, String], session: IHTTPSession, originalUri: String): Response =
    var uri = originalUri.trim.replace(File.separatorChar, '/')
    val queryIndex = uri.indexOf('?')
    if queryIndex >= 0 then uri = uri.substring(0, queryIndex)
    if uri.contains("../") then return getForbiddenResponse("Won't serve ../ for security reasons.")
    var homeDir: File = null
    var canServe = false
    val roots = rootDirs.iterator()
    while roots.hasNext && !canServe do
      homeDir = roots.next()
      canServe = canServeUri(uri, homeDir)
    if !canServe then return getNotFoundResponse
    val file = new File(homeDir, uri)
    if file.isDirectory && !uri.endsWith("/") then
      val redirectUri = uri + "/"
      val response = fixedResponse(Status.REDIRECT, NanoHTTPD.MIME_HTML,
        "<html><body>Redirected: <a href=\"" + redirectUri + "\">" + redirectUri + "</a></body></html>")
      response.addHeader("Location", redirectUri)
      return response
    if file.isDirectory then
      val indexFile = findIndexFileInDirectory(file)
      if indexFile == null then
        if file.canRead then return fixedResponse(Status.OK, NanoHTTPD.MIME_HTML, listDirectory(uri, file))
        return getForbiddenResponse("No directory listing.")
      return respond(headers, session, uri + indexFile)
    val mime = NanoHTTPD.getMimeTypeForFile(uri)
    val plugin = WebServerMain.mimeTypeHandlers.get(mime)
    if plugin != null && plugin.canServeUri(uri, homeDir) then
      val response = plugin.serveFile(uri, headers, session, file, mime)
      if response.isInstanceOf[InternalRewrite] then
        val rewrite = response.asInstanceOf[InternalRewrite]
        return respond(rewrite.getHeaders, session, rewrite.getUri)
      if response != null then response else getNotFoundResponse
    else
      val response = serveFile(headers, file, mime)
      if response != null then response else getNotFoundResponse

  private def listDirectory(uri: String, directory: File): String =
    val heading = "Directory " + uri
    val message = new StringBuilder("<html><head><title>").append(heading)
      .append("</title></head><body><h1>").append(heading).append("</h1><ul>")
    Option(directory.listFiles).toSeq.flatten.sortBy(_.getName).foreach { file =>
      val name = file.getName + (if file.isDirectory then "/" else "")
      message.append("<li><a href=\"").append(encodeUri(uri + name)).append("\">").append(name).append("</a></li>")
    }
    message.append("</ul></body></html>").toString

  override def serve(session: IHTTPSession): Response =
    val headers = session.getHeaders
    val parms = session.getParms
    if !quiet then
      println(session.getMethod.toString + " '" + session.getUri + "' ")
      headers.asScala.foreach { case (name, value) =>
        println("  HDR: '" + name + "' = '" + value + "'")
      }
      parms.asScala.foreach { case (name, value) =>
        println("  PRM: '" + name + "' = '" + value + "'")
      }
    val roots = rootDirs.iterator()
    while roots.hasNext do
      val root = roots.next()
      if !root.isDirectory then return getInternalErrorResponse("given path is not a directory (" + root + ").")
    respond(Collections.unmodifiableMap(headers), session, session.getUri)

  private def serveFile(headers: Map[String, String], file: File, mime: String): Response =
    try
      val etag = Integer.toHexString((file.getAbsolutePath + file.lastModified + file.length).hashCode)
      val range = Option(headers.get("range"))
      var startFrom = 0L
      var endAt = -1L
      range.filter(_.startsWith("bytes=")).foreach { value =>
        val bounds = value.substring(6).split("-", -1)
        if bounds(0).nonEmpty then startFrom = bounds(0).toLong
        if bounds.length > 1 && bounds(1).nonEmpty then endAt = bounds(1).toLong
      }
      val fileLength = file.length
      val ifNoneMatch = headers.get("if-none-match")
      val notModified = ifNoneMatch != null && (ifNoneMatch == "*" || ifNoneMatch == etag)
      if range.nonEmpty && startFrom >= fileLength then
        val response = fixedResponse(Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "")
        response.addHeader("Content-Range", "bytes */" + fileLength)
        response
      else if notModified then
        val response = fixedResponse(Status.NOT_MODIFIED, mime, "")
        response.addHeader("ETag", etag)
        response
      else if range.nonEmpty then
        if endAt < 0 then endAt = fileLength - 1
        val length = math.max(0L, endAt - startFrom + 1)
        val stream = new FileInputStream(file)
        stream.skip(startFrom)
        val response = Response.newFixedLengthResponse(Status.PARTIAL_CONTENT, mime, stream, length)
        response.addHeader("Content-Range", s"bytes $startFrom-$endAt/$fileLength")
        response.addHeader("ETag", etag)
        response
      else
        val response = Response.newFixedLengthResponse(Status.OK, mime, new FileInputStream(file), fileLength)
        response.addHeader("Accept-Ranges", "bytes")
        response.addHeader("Content-Length", fileLength.toString)
        response.addHeader("ETag", etag)
        response
    catch case _: IOException | _: NumberFormatException => getForbiddenResponse("Reading file failed.")

  private def addCORSHeaders(response: Response): Response =
    response.addHeader("Access-Control-Allow-Origin", cors)
    response.addHeader("Access-Control-Allow-Headers", System.getProperty("AccessControlAllowHeader", "origin,accept,content-type"))
    response.addHeader("Access-Control-Allow-Credentials", "true")
    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
    response.addHeader("Access-Control-Max-Age", (42 * 60 * 60).toString)
    response
