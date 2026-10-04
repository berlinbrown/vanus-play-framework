/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.layout

import framework.html.Html
import framework.html.Attributes
import framework.html.HtmlDsl.*
import framework.mvc.Layout

final class MainLayout(titleText: String, css: String) extends Layout:
  override def wrap(content: Html): Html =
    html(xmlns := "http://www.w3.org/1999/xhtml", lang := "en")(
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", Attributes.content := "width=device-width,initial-scale=1"),
        titleTag(titleText),
        style(css)
      ),
      body(content)
    )
