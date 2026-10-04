/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.model

import framework.mvc.Model

final case class Message(content: String, timestamp: String, id: String, role: String) extends Model
