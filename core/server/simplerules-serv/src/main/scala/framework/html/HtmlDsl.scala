/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

object HtmlDsl:
  val id = Attributes.id
  val cls = Attributes.cls
  val href = Attributes.href
  val src = Attributes.src
  val alt = Attributes.alt
  val title = Attributes.title
  val lang = Attributes.lang
  val charset = Attributes.charset
  val name = Attributes.name
  val content = Attributes.content
  val `type` = Attributes.`type`
  val action = Attributes.action
  val value = Attributes.value
  val role = Attributes.role
  val rel = Attributes.rel
  val target = Attributes.target
  val xmlns = Attributes.xmlns
  val viewport = Attributes.viewport

  val html = new Tag("html")
  val head = new Tag("head")
  val body = new Tag("body")
  val titleTag = new Tag("title")
  val style = new Tag("style")
  val main = new Tag("main")
  val header = new Tag("header")
  val footer = new Tag("footer")
  val nav = new Tag("nav")
  val section = new Tag("section")
  val article = new Tag("article")
  val div = new Tag("div")
  val span = new Tag("span")
  val p = new Tag("p")
  val h1 = new Tag("h1")
  val h2 = new Tag("h2")
  val h3 = new Tag("h3")
  val ul = new Tag("ul")
  val ol = new Tag("ol")
  val li = new Tag("li")
  val a = new Tag("a")
  val strong = new Tag("strong")
  val em = new Tag("em")
  val label = new Tag("label")
  val form = new Tag("form")
  val button = new Tag("button")
  val table = new Tag("table")
  val caption = new Tag("caption")
  val thead = new Tag("thead")
  val tbody = new Tag("tbody")
  val tr = new Tag("tr")
  val th = new Tag("th")
  val td = new Tag("td")
  val meta = new VoidTag("meta")
  val link = new VoidTag("link")
  val img = new VoidTag("img")
  val input = new VoidTag("input")
  val br = new VoidTag("br")
  val hr = new VoidTag("hr")

  def text(value: String): Html = Html.text(value)
  def fragment(children: IterableOnce[Html]): Html = Html.fragment(children)
