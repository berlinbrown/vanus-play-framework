/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages

import app.messages.controller.MessagesController
import app.messages.model.Message
import app.messages.service.{HttpMessageService, MessageService}
import framework.mvc.{RequestContext, ResponseAdapter}
import org.nanohttpd.protocols.http.response.Status

class MessagesMvcSuite extends munit.FunSuite:
  private val Context = RequestContext("/vanus-home-messages.van", "GET", Map.empty)

  test("message service parses JSON into named models") {
    val json = """{"vanus":{"messages":[{"message":"hello","timestamp":"2026-09-21T22:01:56Z","id":"144","role":"response"}]}}"""
    val messages = new HttpMessageService("http://unused", "secret", (_, _) => json).loadMessages()

    assertEquals(messages, Vector(Message("hello", "2026-09-21T22:01:56Z", "144", "response")))
  }

  test("messages controller adapts successful views with no-store and escaped content") {
    val service = new MessageService:
      override def loadMessages(): Vector[Message] =
        Vector(Message("<script>not markup</script>", "2026-09-21T22:01:56Z", "id-1", "response"))
    val response = ResponseAdapter.toNanoHttpd(new MessagesController(service).handle(Context))
    val body = new String(response.getData.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)

    assertEquals(response.getStatus, Status.OK)
    assertEquals(response.getHeader("cache-control"), "no-store")
    assert(body.contains("&lt;script&gt;not markup&lt;/script&gt;"))
    assert(!body.contains("<script>not markup"))
  }

  test("malformed and unavailable service responses become generic 503 pages") {
    val failures = Vector[MessageService](
      new HttpMessageService("http://unused", "secret-token", (_, _) => "not json"),
      new MessageService:
        override def loadMessages(): Vector[Message] = throw new IllegalStateException("secret-token leaked")
    )

    failures.foreach { service =>
      val response = ResponseAdapter.toNanoHttpd(new MessagesController(service).handle(Context))
      val body = new String(response.getData.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)
      assertEquals(response.getStatus, Status.SERVICE_UNAVAILABLE)
      assertEquals(response.getHeader("cache-control"), "no-store")
      assert(body.contains("Messages unavailable"))
      assert(!body.contains("secret-token"))
      assert(!body.contains("leaked"))
    }
  }
