/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import app.messages.controller.MessagesController
import app.messages.service.HttpMessageService
import framework.mvc.{RequestContext, ResponseAdapter}
import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.Response

object VanusHomeMessagesHandler:
  val Path = "/vanus-home-messages.van"
  def fetch(url: String, token: String): String = HttpMessageService.fetch(url, token)

final class VanusHomeMessagesHandler(
    messagesUrl: String,
    messagesToken: String,
    load: (String, String) => String = VanusHomeMessagesHandler.fetch
) extends NanoletHandler:
  private val controller = new MessagesController(new HttpMessageService(messagesUrl, messagesToken, load))

  override def get(session: IHTTPSession): Response =
    val context = if session == null then RequestContext(VanusHomeMessagesHandler.Path, "GET", Map.empty)
      else RequestContext.from(session)
    ResponseAdapter.toNanoHttpd(controller.handle(context))
