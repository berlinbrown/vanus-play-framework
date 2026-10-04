/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.resources

import java.io.ByteArrayInputStream

import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.request.Method
import org.nanohttpd.protocols.http.response.{Response, Status}

import scala.util.Using

object ClasspathResources:
  private val PublicUrl = "/rules"
  private val PrivateUrl = "/post"
  private val PublicResource = "META-INF/nanohttpd/pub_html/simplerules/rules"
  private val PrivateResource = "META-INF/nanohttpd/sec_html/simplerules/post"

  def isPrivatePath(uri: String): Boolean = uri == PrivateUrl || uri.startsWith(PrivateUrl + "/")

  def serve(uri: String, session: IHTTPSession, privateResourceAuthorizer: IHTTPSession => Boolean): Option[Response] =
    if uri == PublicUrl || uri == PrivateUrl then
      val response = Response.newFixedLengthResponse(Status.REDIRECT, "text/html",
        "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Redirect</title></head><body></body></html>")
      response.addHeader("Location", uri + "/")
      Some(response)
    else if uri == PublicUrl + "/" || uri.startsWith(PublicUrl + "/") then
      serveResource(uri, PublicUrl, PublicResource, "index.html", session.getMethod == Method.HEAD)
    else if isPrivatePath(uri) then
      if !privateResourceAuthorizer(session) then
        Some(Response.newFixedLengthResponse(Status.UNAUTHORIZED, "text/plain", "Authentication required"))
      else
        serveResource(uri, PrivateUrl, PrivateResource, "home.html", session.getMethod == Method.HEAD)
    else None

  private def serveResource(uri: String, urlRoot: String, resourceRoot: String, defaultFile: String,
      head: Boolean): Option[Response] =
    val relativePath = if uri == urlRoot + "/" then defaultFile else uri.stripPrefix(urlRoot + "/")
    val segments = relativePath.split("/", -1)
    if segments.exists(segment => segment.isEmpty || segment == "." || segment == "..") then
      Some(Response.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "Not found"))
    else
      val resourcePath = s"$resourceRoot/$relativePath"
      val mime =
        if relativePath.endsWith(".xhtml") then Some("application/xhtml+xml")
        else if relativePath.endsWith(".html") || relativePath.endsWith(".htm") then Some("text/html")
        else None
      mime.flatMap { contentType =>
        Option(getClass.getClassLoader.getResourceAsStream(resourcePath)).map { stream =>
          val bytes = Using.resource(stream)(_.readAllBytes())
          val body = if head then Array.emptyByteArray else bytes
          Response.newFixedLengthResponse(Status.OK, contentType,
            new ByteArrayInputStream(body), bytes.length.toLong)
        }
      }.orElse(Some(Response.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "Not found")))
