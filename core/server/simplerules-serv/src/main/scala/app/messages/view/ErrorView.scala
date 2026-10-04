/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.messages.view

import app.layout.MainLayout
import framework.html.Html
import framework.html.HtmlDsl.*
import framework.mvc.View

final class ErrorView extends View:
  override def render: Html =
    new MainLayout("Vanus Messages", MessagesStyles.Css).wrap(
      main(cls := "error")(
        span(cls := "eyebrow")("VANUS PLAY"),
        h1("Messages unavailable"),
        p("The message service could not be reached. Please try again shortly.")
      )
    )
