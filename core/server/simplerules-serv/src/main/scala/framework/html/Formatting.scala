/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

object Formatting:
  val DefaultHtmlIndentSpaces = 2
  val DefaultCssIndentSpaces = 2
  val DefaultNewline = "\n"

final case class Formatting(
    htmlIndentSpaces: Int = Formatting.DefaultHtmlIndentSpaces,
    cssIndentSpaces: Int = Formatting.DefaultCssIndentSpaces,
    newline: String = Formatting.DefaultNewline
):
  require(htmlIndentSpaces >= 0, "HTML indentation cannot be negative")
  require(cssIndentSpaces >= 0, "CSS indentation cannot be negative")
  require(newline.nonEmpty, "Newline cannot be empty")
