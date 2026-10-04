/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.service

import app.messages.model.Message
import java.net.{URI, URLEncoder}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.time.Duration
import scala.util.Using

trait MessageService:
  def loadMessages(): Vector[Message]

final class HttpMessageService(
    messagesUrl: String,
    messagesToken: String,
    loadText: (String, String) => String = HttpMessageService.fetch
) extends MessageService:
  override def loadMessages(): Vector[Message] = parse(loadText(messagesUrl, messagesToken))

  private def parse(json: String): Vector[Message] =
    ujson.read(json)("vanus")("messages").arr.toVector.map { item =>
      val fields = item.obj
      Message(
        fields.get("message").map(_.str).getOrElse(""),
        fields.get("timestamp").map(_.str).getOrElse(""),
        fields.get("id").map(_.str).getOrElse(""),
        fields.get("role").map(_.str).filter(Set("prompt", "response")).getOrElse("message")
      )
    }

object HttpMessageService:
  private val MaxResponseBytes = 1024 * 1024
  private val client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(2))
    .followRedirects(HttpClient.Redirect.NEVER)
    .build()

  private def endpoint(url: String, token: String): URI =
    val separator = if url.contains("?") then "&" else "?"
    URI.create(url + separator + "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8))

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
