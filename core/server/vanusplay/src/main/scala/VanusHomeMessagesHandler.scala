import java.net.{URI, URLEncoder}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.logging.{Level, Logger}

import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.{Response, Status}

import scala.util.control.NonFatal
import scala.util.Using

object VanusHomeMessagesHandler:
  val Path = "/vanus-home-messages.van"
  private val MaxResponseBytes = 1024 * 1024
  private val Log = Logger.getLogger("vanus.messages")

  private def endpoint(url: String, token: String): URI =
    val separator = if url.contains("?") then "&" else "?"
    URI.create(url + separator + "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8))

  private val client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(2))
    .followRedirects(HttpClient.Redirect.NEVER)
    .build()

  def fetch(url: String, token: String): String =
    val request = HttpRequest.newBuilder(endpoint(url, token))
      .timeout(Duration.ofSeconds(4))
      .header("Accept", "application/json")
      .GET()
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
    if response.statusCode() != 200 then
      response.body().close()
      throw new IllegalStateException(s"Messages service returned HTTP ${response.statusCode()}")
    val bytes = Using.resource(response.body())(_.readNBytes(MaxResponseBytes + 1))
    if bytes.length > MaxResponseBytes then throw new IllegalStateException("Messages response is too large")
    new String(bytes, StandardCharsets.UTF_8)

final class VanusHomeMessagesHandler(
    messagesUrl: String,
    messagesToken: String,
    load: (String, String) => String = VanusHomeMessagesHandler.fetch
) extends NanoletHandler:
  override def get(session: IHTTPSession): Response =
    try
      val messages = parse(load(messagesUrl, messagesToken))
      html(Status.OK, render(messages))
    catch
      case NonFatal(error) =>
        VanusHomeMessagesHandler.Log.log(
          Level.WARNING,
          "Unable to load or render the Vanus messages page",
          error
        )
        html(Status.SERVICE_UNAVAILABLE, renderError)

  private def parse(json: String): Vector[(String, String, String, String)] =
    ujson.read(json)("vanus")("messages").arr.toVector.map { item =>
      val obj = item.obj
      (
        obj.get("message").map(_.str).getOrElse(""),
        obj.get("timestamp").map(_.str).getOrElse(""),
        obj.get("id").map(_.str).getOrElse(""),
        obj.get("role").map(_.str).filter(Set("prompt", "response")).getOrElse("message")
      )
    }

  private def html(status: Status, body: String): Response =
    val response = Response.newFixedLengthResponse(status, "text/html; charset=utf-8", body)
    response.addHeader("Cache-Control", "no-store")
    response

  private def render(messages: Vector[(String, String, String, String)]): String =
    val cards = messages.map { case (message, timestamp, id, role) =>
      val label = if role == "prompt" then "You" else if role == "response" then "Vanus" else "Message"
      s"""<article class="message $role">
         |<header><span class="role">${escape(label)}</span><span class="meta">#${escape(id)} · ${escape(formatTimestamp(timestamp))}</span></header>
         |<p>${escape(message)}</p>
         |</article>""".stripMargin
    }.mkString("\n")
    page(s"""<header class="hero"><div><span class="eyebrow">VANUS PLAY</span><h1>Message stream</h1><p>Recent prompts and responses from the Vanus bot.</p></div><span class="count">${messages.size} messages</span></header>
            |<main class="stream">$cards</main>""".stripMargin)

  private def renderError: String = page(
    """<main class="error"><span class="eyebrow">VANUS PLAY</span><h1>Messages unavailable</h1><p>The message service could not be reached. Please try again shortly.</p></main>""")

  private def formatTimestamp(value: String): String =
    value.replace('T', ' ').stripSuffix("Z") + (if value.endsWith("Z") then " UTC" else "")

  private def escape(value: String): String = VanusSecurity.escapeHtml(value)

  private def page(content: String): String =
    s"""<!doctype html>
       |<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
       |<title>Vanus Messages</title><style>${VanusHomeMessagesStyles.Css}</style>
       |</head><body>$content</body></html>""".stripMargin
