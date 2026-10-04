/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.view

import app.layout.MainLayout
import app.messages.component.MessageCard
import app.messages.model.Message
import framework.html.Html
import framework.html.HtmlDsl.*
import framework.mvc.View

final class MessagesView(messages: Vector[Message]) extends View:
  override def render: Html =
    val cards = messages.map(MessageCard.render)
    new MainLayout("Vanus Messages", MessagesStyles.Css).wrap(
      fragment(Vector(
        header(cls := "hero")(
          div(
            span(cls := "eyebrow")("VANUS PLAY"),
            h1("Message stream"),
            p("Recent prompts and responses from the Vanus bot.")
          ),
          span(cls := "count")(s"${messages.size} messages")
        ),
        main(cls := "stream")(cards*)
      ))
    )
