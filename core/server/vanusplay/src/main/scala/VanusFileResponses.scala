import java.io.{File, FileInputStream, IOException}
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.response.{Response, Status}
import org.nanohttpd.protocols.http.response.IStatus

object VanusFileResponses:
  def fixedResponse(status: IStatus, mime: String, message: String): Response =
    withCommonHeaders(Response.newFixedLengthResponse(status, mime, message), mime)

  def serveFile(headers: java.util.Map[String, String], file: File, mime: String): Response =
    try
      val etag = Integer.toHexString((file.getAbsolutePath + file.lastModified + file.length).hashCode)
      val range = Option(headers.get(VanusConstants.HeaderRange))
      var startFrom = 0L
      var endAt = -1L
      range.filter(_.startsWith("bytes=")).foreach { value =>
        val bounds = value.substring(6).split("-", -1)
        if bounds(0).nonEmpty then startFrom = bounds(0).toLong
        if bounds.length > 1 && bounds(1).nonEmpty then endAt = bounds(1).toLong
      }
      val fileLength = file.length
      val ifNoneMatch = headers.get(VanusConstants.HeaderIfNoneMatch)
      val notModified = ifNoneMatch != null && (ifNoneMatch == "*" || ifNoneMatch == etag)
      if range.nonEmpty && startFrom >= fileLength then
        val response = fixedResponse(Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "")
        response.addHeader(VanusConstants.HeaderContentRange, "bytes */" + fileLength)
        response
      else if notModified then
        val response = fixedResponse(Status.NOT_MODIFIED, mime, "")
        response.addHeader(VanusConstants.HeaderETag, etag)
        response
      else if range.nonEmpty then
        if endAt < 0 then endAt = fileLength - 1
        val length = math.max(0L, endAt - startFrom + 1)
        val stream = new FileInputStream(file)
        stream.skip(startFrom)
        val response = withCommonHeaders(Response.newFixedLengthResponse(Status.PARTIAL_CONTENT, mime, stream, length), mime)
        response.addHeader(VanusConstants.HeaderContentRange, s"bytes $startFrom-$endAt/$fileLength")
        response.addHeader(VanusConstants.HeaderETag, etag)
        response
      else
        val response = withCommonHeaders(Response.newFixedLengthResponse(Status.OK, mime, new FileInputStream(file), fileLength), mime)
        response.addHeader(VanusConstants.HeaderContentLength, fileLength.toString)
        response.addHeader(VanusConstants.HeaderETag, etag)
        response
    catch case _: IOException | _: NumberFormatException => forbidden(VanusConstants.ErrorReadingFileFailed)

  private def withCommonHeaders(response: Response, mime: String): Response =
    response.addHeader(VanusConstants.HeaderAcceptRanges, "bytes")
    if isHtmlMime(mime) then
      response.addHeader(VanusConstants.HeaderContentSecurityPolicy, VanusConstants.ContentSecurityPolicyValue)
    response

  private def isHtmlMime(mime: String): Boolean =
    Option(mime).exists(_.toLowerCase.contains("html"))

  private def forbidden(message: String): Response =
    Response.newFixedLengthResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, VanusConstants.ErrorForbiddenPrefix + message)
