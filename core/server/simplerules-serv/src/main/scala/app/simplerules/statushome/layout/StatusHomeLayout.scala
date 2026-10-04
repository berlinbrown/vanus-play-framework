/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.layout

import content.ContentStyles
import framework.html.Html
import framework.html.Attributes
import framework.html.HtmlDsl.*
import framework.mvc.Layout

final class StatusHomeLayout(titleText: String) extends Layout:
  override def wrap(content: Html): Html =
    html(xmlns := "http://www.w3.org/1999/xhtml", lang := "en")(
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", Attributes.content := "width=device-width,initial-scale=1"),
        titleTag(titleText),
        style(ContentStyles.Css)
      ),
      body(content)
    )

object StatusHomeStyles:
  val Css: String = ContentStyles.Css
  val CspHash: String = ContentStyles.CspHash
