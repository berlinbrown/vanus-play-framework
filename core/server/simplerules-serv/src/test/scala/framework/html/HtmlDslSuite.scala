/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

import munit.FunSuite

class HtmlDslSuite extends FunSuite:
  import HtmlDsl.*

  test("text escapes markup and quotes") {
    assertEquals(
      HtmlRenderer.render(p("<tag> & \"quoted\" 'single'")),
      "<p>&lt;tag&gt; &amp; \"quoted\" 'single'</p>"
    )
  }

  test("attributes are escaped and unsafe names and URL schemes are rejected") {
    assertEquals(HtmlRenderer.render(a(href := "/search?a=1&b=2", title := "\"hello\"")(text("go"))),
      "<a href=\"/search?a=1&amp;b=2\" title=\"&quot;hello&quot;\">go</a>")
    intercept[IllegalArgumentException](Attribute.name("bad name"))
    intercept[IllegalArgumentException](Attribute.name("onclick"))
    intercept[IllegalArgumentException](href := "javascript:alert(1)")
    intercept[IllegalArgumentException](href := "data:text/html,unsafe")
  }

  test("nested elements, fragments, collections, and empty fragments render predictably") {
    val cards = Vector(article(cls := "message")(h2("One")), article(cls := "message")(h2("Two")))
    val page = main(fragment(cards))
    assertEquals(HtmlRenderer.render(page),
      "<main><article class=\"message\"><h2>One</h2></article><article class=\"message\"><h2>Two</h2></article></main>")
    assertEquals(HtmlRenderer.render(main(fragment(Vector.empty))), "<main></main>")
  }

  test("conditional nodes and collection mapping use ordinary Scala expressions") {
    val names = Vector("Ada", "Grace")
    val greeting = if names.nonEmpty then p(names.mkString(", ")) else Html.empty
    val list = ul(cls := "names")(names.map(name => li(name)))
    assertEquals(HtmlRenderer.render(fragment(Vector(greeting, list))),
      "<p>Ada, Grace</p><ul class=\"names\"><li>Ada</li><li>Grace</li></ul>")
  }

  test("HTML5 void elements render without an end tag and reject children") {
    assertEquals(HtmlRenderer.render(fragment(Vector(meta(charset := "utf-8"), br(), img(src := "/logo.png", alt := "Logo")))),
      "<meta charset=\"utf-8\" /><br /><img src=\"/logo.png\" alt=\"Logo\" />")
    intercept[IllegalArgumentException](Element.create("img", Nil, Vector(text("invalid"))))
  }

  test("documents and embedded CSS use configurable framework formatting") {
    val document = html(xmlns := "http://www.w3.org/1999/xhtml")(
      head(style("body{margin:0;color:#111}")),
      body(div(div(p("Nested"))))
    )
    val rendered = HtmlRenderer.renderDocument(document, Formatting(
      htmlIndentSpaces = 2,
      cssIndentSpaces = 2
    ))

    assert(rendered.contains("\n  <head>\n    <style>"))
    assert(rendered.contains("\n      body {\n        margin:0;\n        color:#111\n      }"))
    assert(rendered.contains("\n    <div>\n      <div>\n        <p>Nested</p>"))
    assertEquals(Formatting.DefaultHtmlIndentSpaces, 2)
    assertEquals(Formatting.DefaultCssIndentSpaces, 2)
    assertEquals(HtmlRenderer.renderDocumentCompact(p("compact")), "<!DOCTYPE html><p>compact</p>")
  }
