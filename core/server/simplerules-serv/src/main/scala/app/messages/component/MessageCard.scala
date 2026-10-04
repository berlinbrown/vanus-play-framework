/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.component

import app.messages.model.Message
import framework.html.Html
import framework.html.HtmlDsl.*

object MessageCard:
  def render(message: Message): Html =
    val labelText = message.role match
      case "prompt" => "You"
      case "response" => "Vanus"
      case _ => "Message"
    article(cls := s"message ${message.role}")(
      header(
        span(cls := "role")(text(labelText)),
        span(cls := "meta")(text(s"#${message.id} · ${formatTimestamp(message.timestamp)}"))
      ),
      p(message.content)
    )

  private def formatTimestamp(value: String): String =
    value.replace('T', ' ').stripSuffix("Z") + (if value.endsWith("Z") then " UTC" else "")
