/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.service

import app.simplerules.statushome.model.{NodeState, NodeStatus, SystemStatus}

trait StatusService:
  def currentStatus(): SystemStatus

final class DemoStatusService extends StatusService:
  override def currentStatus(): SystemStatus = SystemStatus(
    serviceName = "SimpleRules Web",
    state = NodeState.Online,
    checkedAt = "2026-10-03 18:00 UTC",
    nodes = Vector(
      NodeStatus("Node 1", NodeState.Online, "127.0.0.1:8085", "1.4.2", "12d 04h", Some(18)),
      NodeStatus("Node 2", NodeState.Online, "127.0.0.1:8081", "1.4.2", "8d 17h", Some(24)),
      NodeStatus("Node 3", NodeState.Degraded, "127.0.0.1:8082", "1.4.1", "2d 03h", Some(186))
    )
  )
