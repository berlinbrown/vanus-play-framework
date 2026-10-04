/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.mvc

import app.simplerules.statushome.controller.StatusHomeController
import org.nanohttpd.protocols.http.response.Status

class MvcSuite extends munit.FunSuite:
  test("status-home controller returns an HTML view with the expected status") {
    val result = new StatusHomeController().handle(RequestContext("/simplerules/app/status.van", "GET", Map.empty))
    val response = ResponseAdapter.toNanoHttpd(result)
    val body = new String(response.getData.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)

    assertEquals(response.getStatus, Status.OK)
    assertEquals(response.getMimeType, "text/html; charset=utf-8")
    assert(body.startsWith("<!DOCTYPE html>\n<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">"))
    assert(body.contains("Main Status"))
    assert(body.contains("Server Up"))
    assert(body.contains("Node 1"))
  }

  test("MVC router maps HEAD to a GET controller") {
    val controller = new StatusHomeController
    val router = Router(Map((org.nanohttpd.protocols.http.request.Method.GET,
      "/simplerules/app/status.van") -> controller))

    assertEquals(router.controller(org.nanohttpd.protocols.http.request.Method.HEAD,
      "/simplerules/app/status.van"), Some(controller))
  }
