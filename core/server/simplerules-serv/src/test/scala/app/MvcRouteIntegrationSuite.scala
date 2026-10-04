/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.file.Files
import org.nanohttpd.protocols.http.request.Method

class MvcRouteIntegrationSuite extends munit.FunSuite:
  test("status MVC route is served through NanoHTTPD") {
    val root = Files.createTempDirectory("mvc-route-").toFile
    val path = app.simplerules.statushome.controller.StatusHomeController.Path
    val handler = new MvcNanoletHandler(framework.mvc.Router(Map(
      (Method.GET, path) -> new app.simplerules.statushome.controller.StatusHomeController
    )))
    val server = new WebServer("127.0.0.1", 0, java.util.List.of(root), true, false, null, null,
      routes = Map((Method.GET, path) -> handler))
    try
      server.start(5000, false)
      val request = HttpRequest.newBuilder(URI.create(s"http://127.0.0.1:${server.getListeningPort}$path")).GET().build()
      val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

      assertEquals(response.statusCode(), 200)
      assertEquals(response.headers().firstValue("content-type").orElse(""), VanusSecurity.HtmlContentType)
      assert(response.body().contains("Main Status"))
      assert(response.body().contains("Server Up"))
      assert(response.body().contains("Node 1"))
      assert(response.headers().firstValue("content-security-policy").isPresent)
    finally
      server.stop()
      Files.deleteIfExists(root.toPath)
  }
