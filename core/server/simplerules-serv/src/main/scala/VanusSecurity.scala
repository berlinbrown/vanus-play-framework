/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import java.util.Locale
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import org.nanohttpd.protocols.http.response.{Response, Status}
import org.apache.logging.log4j.LogManager

object VanusSecurity:
  val contentTypes: Map[String, String] = Map(
    "html" -> "text/html", "htm" -> "text/html", "xhtml" -> "application/xhtml+xml",
    "css" -> "text/css", "txt" -> "text/plain",
    "png" -> "image/png", "jpg" -> "image/jpeg", "jpeg" -> "image/jpeg",
    "gif" -> "image/gif", "ico" -> "image/x-icon", "webp" -> "image/webp"
  )
  private val allowedMimeTypes = contentTypes.values.toSet
  private val MarkupMimeTypes = Set("text/html", "application/xhtml+xml")
  private val StyleElement = "(?s)<style(?:\\s[^>]*)?>(.*?)</style>".r
  val HtmlContentType = "application/xhtml+xml; charset=UTF-8"
  private val Log = LogManager.getLogger("vanus.markup")

  def mimeType(path: String): Option[String] =
    val name = path.substring(path.lastIndexOf('/') + 1)
    val dot = name.lastIndexOf('.')
    if dot < 0 then None else contentTypes.get(name.substring(dot + 1).toLowerCase(Locale.ROOT))

  def protect(response: Response): Response =
    val mime = Option(response.getMimeType).getOrElse("").takeWhile(_ != ';').trim.toLowerCase(Locale.ROOT)
    var markupBytes = Option.empty[Array[Byte]]
    val safe = if !allowedMimeTypes.contains(mime) || response.getMimeType.exists(_.isControl) then
      response.close()
      Response.newFixedLengthResponse(Status.FORBIDDEN, "text/plain", "Response type is not allowed")
    else if MarkupMimeTypes.contains(mime) && bodyMustBeValidated(response) then
      try
        val input = response.getData
        if input != null then
          input.mark(Int.MaxValue)
          val bytes = input.readAllBytes()
          input.reset()
          VanusXhtmlValidator.validate(bytes)
          markupBytes = Some(bytes)
        response
      catch
        case error: VanusMarkupException =>
          response.close()
          Log.error("Invalid XHTML markup in response", error)
          Response.newFixedLengthResponse(Status.INTERNAL_ERROR, "text/plain", "Internal server error")
    else response
    val finalContentType =
      if MarkupMimeTypes.contains(Option(safe.getMimeType).getOrElse("").takeWhile(_ != ';').trim.toLowerCase(Locale.ROOT))
      then HtmlContentType
      else safe.getMimeType
    safe.addHeader("Content-Type", finalContentType)
    safe.addHeader("X-Content-Type-Options", "nosniff")
    safe.addHeader("Content-Security-Policy", contentSecurityPolicy(markupBytes))
    safe

  private def contentSecurityPolicy(markupBytes: Option[Array[Byte]]): String =
    val hashes = markupBytes.toVector.flatMap { bytes =>
      val markup = new String(bytes, StandardCharsets.UTF_8)
      StyleElement.findAllMatchIn(markup).map { matched =>
        val digest = MessageDigest.getInstance("SHA-256")
          .digest(matched.group(1).getBytes(StandardCharsets.UTF_8))
        "'sha256-" + Base64.getEncoder.encodeToString(digest) + "'"
      }
    }.distinct
    if hashes.isEmpty then VanusConstants.ContentSecurityPolicyValue
    else VanusConstants.ContentSecurityPolicyValue.replace(
      "style-src 'self'", s"style-src 'self' ${hashes.mkString(" ")}")

  private def bodyMustBeValidated(response: Response): Boolean =
    val status = response.getStatus.getRequestStatus
    status >= 200 && status != 204 && status != 304 && response.getRequestMethod != org.nanohttpd.protocols.http.request.Method.HEAD &&
      (response.getData == null || response.getData.markSupported())

  def escapeHtml(value: String): String =
    value.flatMap {
      case '&' => "&amp;"
      case '<' => "&lt;"
      case '>' => "&gt;"
      case '"' => "&quot;"
      case '\'' => "&#39;"
      case c => c.toString
    }
