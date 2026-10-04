/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.controller

import app.messages.service.MessageService
import app.messages.view.{ErrorView, MessagesView}
import framework.mvc.{Controller, RequestContext, ViewResult}
import org.nanohttpd.protocols.http.response.Status
import org.apache.logging.log4j.LogManager
import scala.util.control.NonFatal

final class MessagesController(service: MessageService) extends Controller:
  private val noStore = Map("Cache-Control" -> "no-store")

  override def handle(context: RequestContext): ViewResult =
    try ViewResult(Status.OK, new MessagesView(service.loadMessages()), noStore)
    catch
      case NonFatal(error) =>
        MessagesController.Log.warn(
          s"Unable to load or render the Vanus messages page (${error.getClass.getSimpleName})")
        ViewResult(Status.SERVICE_UNAVAILABLE, new ErrorView, noStore)

object MessagesController:
  private val Log = LogManager.getLogger("vanus.messages")
