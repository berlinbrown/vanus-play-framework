/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.view

import app.simplerules.statushome.model.{NodeState, NodeStatus, SystemStatus}
import framework.html.HtmlRenderer

class StatusHomeViewSuite extends munit.FunSuite:
  test("renders status summary and a table row per node") {
    val status = new app.simplerules.statushome.service.DemoStatusService().currentStatus()
    val body = HtmlRenderer.renderDocument(new StatusHomeView(status).render)

    assert(body.contains("Main Status"))
    assert(body.contains("Server Up"))
    assert(body.contains("<table>"))
    assert(body.contains("<th>Node</th>"))
    assertEquals("class=\"node-name\"".r.findAllIn(body).length, 3)
    assert(body.contains("Node 1"))
    assert(body.contains("Node 2"))
    assert(body.contains("Node 3"))
    assert(body.contains("Degraded"))
    assert(body.contains("Quick links"))
    assert(body.contains("href=\"https://www.google.com\""))
    assert(body.contains("href=\"/simplerules/app/status.van\""))
    assert(body.contains("href=\"https://www.scala-lang.org\""))
    assert(body.contains("href=\"https://github.com\""))
    assert(body.contains("href=\"https://www.wikipedia.org\""))
    assertEquals("class=\"link-card-inner\"".r.findAllIn(body).length, 6)
  }

  test("renders an empty node state and escapes untrusted node text") {
    val status = SystemStatus(
      "<script>bad</script>",
      NodeState.Offline,
      "now",
      Vector(NodeStatus("<img src=x>", NodeState.Offline, "node&one", "v1", "1h", None))
    )
    val body = HtmlRenderer.renderDocument(new StatusHomeView(status).render)

    assert(body.contains("&lt;script&gt;bad&lt;/script&gt;"))
    assert(body.contains("&lt;img src=x&gt;"))
    assert(!body.contains("<script>bad"))
    assert(!body.contains("<img src=x>"))
    assert(body.contains("node&amp;one"))
    assert(body.contains("Unavailable"))

    val emptyBody = HtmlRenderer.renderDocument(new StatusHomeView(status.copy(nodes = Vector.empty)).render)
    assert(emptyBody.contains("No nodes are configured."))
    assert(emptyBody.contains("0 / 0"))
  }
