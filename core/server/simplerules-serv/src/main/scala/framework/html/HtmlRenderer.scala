/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

object HtmlRenderer:
  private val BlockElements = Set(
    "html", "head", "body", "main", "header", "footer", "nav", "section", "article", "div",
    "p", "h1", "h2", "h3", "ul", "ol", "li", "form", "table", "caption", "thead", "tbody",
    "tr", "th", "td", "style"
  )

  def render(html: Html): String =
    val output = new StringBuilder
    append(html, output)
    output.result()

  // XHTML is parsed as XML by browsers, where declaration keywords are case-sensitive.
  def renderDocument(html: Html, formatting: Formatting = Formatting()): String =
    val output = new StringBuilder("<!DOCTYPE html>").append(formatting.newline)
    appendFormatted(html, output, 0, formatting)
    output.result()

  def renderDocumentCompact(html: Html): String = "<!DOCTYPE html>" + render(html)

  private def append(node: Html, output: StringBuilder): Unit = node match
    case Text(value) => escapeText(value, output)
    case Fragment(children) => children.foreach(append(_, output))
    case element: Element =>
      output.append('<').append(element.name)
      element.attributes.foreach { attribute =>
        Attribute.validateName(attribute.name)
        output.append(' ').append(attribute.name).append("=\"")
        escapeAttribute(attribute.value, output)
        output.append('"')
      }
      if element.isVoid then output.append(" />")
      else
        output.append('>')
        element.children.foreach(append(_, output))
        output.append("</").append(element.name).append('>')

  private def appendFormatted(node: Html, output: StringBuilder, depth: Int,
      formatting: Formatting): Unit = node match
    case Text(value) =>
      output.append(" " * (depth * formatting.htmlIndentSpaces))
      escapeText(value, output)
    case Fragment(children) =>
      children.zipWithIndex.foreach { case (child, index) =>
        appendFormatted(child, output, depth, formatting)
        if index < children.size - 1 then output.append(formatting.newline)
      }
    case element: Element =>
      output.append(" " * (depth * formatting.htmlIndentSpaces))
      appendOpenTag(element, output)
      if element.isVoid then output.append(" />")
      else if element.children.isEmpty then
        output.append("></").append(element.name).append('>')
      else if element.name == "style" && element.children.size == 1 && element.children.head.isInstanceOf[Text] then
        output.append('>').append(formatting.newline)
        val css = element.children.head.asInstanceOf[Text].value
        val formattedCss = CssFormatter.format(css, formatting.cssIndentSpaces,
          formatting.newline, depth + 1)
        formattedCss.foreach {
          case '&' => output.append("&amp;")
          case '<' => output.append("&lt;")
          case '>' => output.append("&gt;")
          case character => output.append(character)
        }
        output.append(formatting.newline)
        output.append(" " * (depth * formatting.htmlIndentSpaces))
        output.append("</").append(element.name).append('>')
      else if shouldExpand(element) then
        output.append('>').append(formatting.newline)
        element.children.zipWithIndex.foreach { case (child, index) =>
          appendFormatted(child, output, depth + 1, formatting)
          if index < element.children.size - 1 then output.append(formatting.newline)
        }
        output.append(formatting.newline)
        output.append(" " * (depth * formatting.htmlIndentSpaces))
        output.append("</").append(element.name).append('>')
      else
        output.append('>')
        element.children.foreach(append(_, output))
        output.append("</").append(element.name).append('>')

  private def shouldExpand(element: Element): Boolean =
    element.children.forall {
      case child: Element => BlockElements.contains(child.name)
      case _: Fragment => true
      case _: Text => false
    }

  private def appendOpenTag(element: Element, output: StringBuilder): Unit =
    output.append('<').append(element.name)
    element.attributes.foreach { attribute =>
      Attribute.validateName(attribute.name)
      output.append(' ').append(attribute.name).append("=\"")
      escapeAttribute(attribute.value, output)
      output.append('"')
    }

  private def escapeText(value: String, output: StringBuilder): Unit =
    value.foreach {
      case '&' => output.append("&amp;")
      case '<' => output.append("&lt;")
      case '>' => output.append("&gt;")
      case character => output.append(character)
    }

  private def escapeAttribute(value: String, output: StringBuilder): Unit =
    value.foreach {
      case '&' => output.append("&amp;")
      case '<' => output.append("&lt;")
      case '>' => output.append("&gt;")
      case '"' => output.append("&quot;")
      case '\'' => output.append("&#39;")
      case character => output.append(character)
    }
