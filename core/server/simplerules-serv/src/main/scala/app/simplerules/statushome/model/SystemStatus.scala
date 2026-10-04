/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.model

import framework.mvc.Model

final case class SystemStatus(
    serviceName: String,
    state: NodeState,
    checkedAt: String,
    nodes: Vector[NodeStatus]
) extends Model
