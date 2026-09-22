import java.net.{URI, URLEncoder}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Duration

import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.{Response, Status}

import scala.util.control.NonFatal
import scala.util.Using

object VanusHomeMessagesHandler:
  val Path = "/vanus-home-messages.van"
  val CssPath = "/vanus-home-messages.css"
  private val MaxResponseBytes = 1024 * 1024

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
      case NonFatal(_) => html(Status.SERVICE_UNAVAILABLE, renderError)

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
       |<title>Vanus Messages</title><link rel="stylesheet" href="${VanusHomeMessagesHandler.CssPath}">
       |</head><body>$content</body></html>""".stripMargin

object VanusHomeMessagesCssHandler extends NanoletHandler:
  val Styles =
    """:root{color-scheme:light;--bg:#fff;--panel:#f8fafc;--line:#dbe3ee;--text:#172033;--muted:#66758c;--cyan:#087f71;--violet:#6657c7}
      |*{box-sizing:border-box}body{margin:0;min-height:100vh;background:linear-gradient(180deg,#f6f9fc 0,#fff 260px);color:var(--text);font:14px/1.5 ui-sans-serif,system-ui,-apple-system,sans-serif}
      |body:before{content:"";display:block;height:3px;background:linear-gradient(90deg,var(--cyan),var(--violet))}.hero,.stream,.error{width:min(880px,calc(100% - 32px));margin-inline:auto}.hero{display:flex;justify-content:space-between;align-items:end;gap:24px;padding:64px 0 28px;border-bottom:1px solid var(--line)}
      |h1{margin:.15em 0;font-size:clamp(1.8rem,4vw,3rem);letter-spacing:-.04em;line-height:1}.hero p,.error p{color:var(--muted);margin:0}.eyebrow{color:var(--cyan);font-size:.68rem;font-weight:800;letter-spacing:.2em}.count{white-space:nowrap;color:var(--muted);font-size:.82rem;border:1px solid var(--line);border-radius:999px;padding:6px 11px;background:#fff}
      |.stream{display:grid;gap:12px;padding:28px 0 64px}.message{max-width:82%;padding:14px 16px;background:var(--panel);border:1px solid var(--line);border-radius:5px 18px 18px 18px;box-shadow:0 8px 24px #27364d12}.message.response{justify-self:end;background:#f7f5ff;border-radius:18px 5px 18px 18px;border-color:#dcd7f7}.message header{display:flex;justify-content:space-between;gap:18px}.role{font-weight:750;color:var(--cyan)}.response .role{color:var(--violet)}.meta{color:var(--muted);font-size:.74rem}.message p{margin:7px 0 0;white-space:pre-wrap;overflow-wrap:anywhere}.error{margin-top:18vh;padding:32px;border:1px solid var(--line);border-radius:20px;background:var(--panel);box-shadow:0 12px 36px #27364d14}
      |@media(max-width:600px){.hero{align-items:start;flex-direction:column;padding-top:42px}.message{max-width:94%}.message header{flex-direction:column;gap:2px}}""".stripMargin

  override def get(session: IHTTPSession): Response =
    val response = Response.newFixedLengthResponse(Status.OK, "text/css; charset=utf-8", Styles)
    response.addHeader("Cache-Control", "public, max-age=3600")
    response
