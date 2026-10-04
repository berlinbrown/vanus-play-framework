/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.{IStatus, Response, Status}
import framework.mvc.{RequestContext, ResponseAdapter, Router}

import scala.jdk.CollectionConverters.*

trait NanoletHandler:
  def get(session: IHTTPSession): Response

final class MvcNanoletHandler(router: Router) extends NanoletHandler:
  override def get(session: IHTTPSession): Response =
    router.controller(session.getMethod, session.getUri) match
      case Some(controller) => ResponseAdapter.toNanoHttpd(controller.handle(RequestContext.from(session)))
      case None => Response.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "Not found")

/**
 * A trait representing a handler for NanoHTTPD requests.
 * Implementations should provide a method to handle GET requests.
 */
abstract class DefaultHandler extends NanoletHandler:
  def getText(session: IHTTPSession): String
  def getMimeType: String
  def getStatus: IStatus
  def getCustomHeaders: Map[String, String] = Map.empty

  override def get(session: IHTTPSession): Response =
    val response = Response.newFixedLengthResponse(getStatus, getMimeType, getText(session))
    getCustomHeaders.foreach { case (name, value) =>
      response.addHeader(name, value)
    }
    response

/**
 * A general-purpose handler that responds with an HTML page displaying
 * the request URL, query parameters, and headers.
 */
class GeneralHandler extends DefaultHandler:
  override def getMimeType: String = "text/html"
  override def getStatus: IStatus = Status.OK
  override def getCustomHeaders: Map[String, String] = Map(
    VanusConstants.HeaderXServerVanusInfo -> VanusConstants.VanusVersionValue
  )

  override def getText(session: IHTTPSession): String =
    val lines = Vector.newBuilder[String]
    
    // Test with JavaScript, it will not execute
    lines += "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
    lines += "  <body>"
    lines += s"    <h1>z data - Vanus Play Web Server</h1><br>"    
    lines += "  </body>"
    lines += "</html>"
    lines.result().mkString("\n")
