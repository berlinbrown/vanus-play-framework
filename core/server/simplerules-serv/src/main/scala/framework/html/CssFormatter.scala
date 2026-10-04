/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

object CssFormatter:
  def format(css: String, indentSpaces: Int = Formatting.DefaultCssIndentSpaces,
      newline: String = Formatting.DefaultNewline, initialDepth: Int = 0): String =
    require(indentSpaces >= 0, "CSS indentation cannot be negative")
    val output = new StringBuilder
    var depth = initialDepth
    var lineStarted = false
    var quote: Char = 0
    var escaped = false

    def indent(): Unit =
      if !lineStarted then
        output.append(" " * (depth * indentSpaces))
        lineStarted = true

    def nextLine(): Unit =
      while output.nonEmpty && output.last == ' ' do output.setLength(output.length - 1)
      if output.nonEmpty && !output.endsWith(newline) then output.append(newline)
      lineStarted = false

    css.trim.foreach { character =>
      if quote != 0 then
        indent()
        output.append(character)
        if escaped then escaped = false
        else if character == '\\' then escaped = true
        else if character == quote then quote = 0
      else character match
        case '\'' | '"' =>
          indent()
          quote = character
          output.append(character)
        case '{' =>
          indent()
          if output.nonEmpty && output.last != ' ' then output.append(' ')
          output.append('{')
          depth += 1
          nextLine()
        case '}' =>
          if lineStarted then nextLine()
          depth = math.max(initialDepth, depth - 1)
          indent()
          output.append('}')
          nextLine()
        case ';' =>
          indent()
          output.append(';')
          nextLine()
        case c if c.isWhitespace =>
          if lineStarted && output.nonEmpty && output.last != ' ' then output.append(' ')
        case c =>
          indent()
          output.append(c)
    }
    while output.nonEmpty && (output.last == '\n' || output.last == '\r' || output.last == ' ') do
      output.setLength(output.length - 1)
    output.result()
