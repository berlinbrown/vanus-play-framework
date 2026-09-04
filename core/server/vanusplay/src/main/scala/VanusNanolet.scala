import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.{IStatus, Response, Status}

import scala.jdk.CollectionConverters.*

trait NanoletHandler:
  def get(session: IHTTPSession): Response

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
    VanusConstants.HeaderXServerVanusInfo -> VanusConstants.VanusVersionValue,
    VanusConstants.HeaderContentSecurityPolicy -> VanusConstants.ContentSecurityPolicyValue
  )

  override def getText(session: IHTTPSession): String =
    val lines = Vector.newBuilder[String]
    
    // Test with JavaScript, it will not execute
    lines += "<html>"
    lines += "  <script>"
    lines += s"    console.log('${session.getUri}');"
    lines += "  </script>"
    lines += "  <body>"
    lines += s"    <h1>Url: ${session.getUri}</h1><br>"

    val queryParams = session.getParms
    if queryParams != null && !queryParams.isEmpty then
      queryParams.asScala.foreach { case (key, value) =>
        lines += s"    <p>Param '$key' = $value</p>"
      }
    else
      lines += "    <p>no params in url</p><br>"

    val headers = session.getHeaders
    if headers != null && !headers.isEmpty then
      lines += "    <h2>Headers</h2>"
      headers.asScala.foreach { case (key, value) =>
        lines += s"    <p>Header '$key' = $value</p>"
      }

    lines += "  </body>"
    lines += "</html>"
    lines.result().mkString("\n")
