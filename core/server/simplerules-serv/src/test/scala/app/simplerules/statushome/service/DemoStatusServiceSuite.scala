/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.service

import app.simplerules.statushome.model.NodeState

class DemoStatusServiceSuite extends munit.FunSuite:
  test("demo service supplies a stable main status and representative node states") {
    val status = new DemoStatusService().currentStatus()

    assertEquals(status.serviceName, "SimpleRules Web")
    assertEquals(status.state, NodeState.Online)
    assertEquals(status.nodes.map(_.name), Vector("Node 1", "Node 2", "Node 3"))
    assertEquals(status.nodes.map(_.state), Vector(NodeState.Online, NodeState.Online, NodeState.Degraded))
    assert(status.nodes.forall(_.responseMillis.nonEmpty))
  }
