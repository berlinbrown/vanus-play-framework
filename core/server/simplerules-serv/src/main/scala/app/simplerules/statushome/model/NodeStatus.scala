/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.model

import framework.mvc.Model

enum NodeState(val label: String):
  case Online extends NodeState("Online")
  case Degraded extends NodeState("Degraded")
  case Offline extends NodeState("Offline")

final case class NodeStatus(
    name: String,
    state: NodeState,
    endpoint: String,
    version: String,
    uptime: String,
    responseMillis: Option[Int]
) extends Model
