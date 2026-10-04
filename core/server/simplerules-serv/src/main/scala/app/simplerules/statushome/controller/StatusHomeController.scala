/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.controller

import app.simplerules.statushome.service.{DemoStatusService, StatusService}
import app.simplerules.statushome.view.StatusHomeView
import framework.mvc.{Controller, RequestContext, ViewResult}
import org.nanohttpd.protocols.http.response.Status

final class StatusHomeController(service: StatusService = new DemoStatusService) extends Controller:
  override def handle(context: RequestContext): ViewResult =
    ViewResult(Status.OK, new StatusHomeView(service.currentStatus()))

object StatusHomeController:
  val Path = "/simplerules/app/status.van"
