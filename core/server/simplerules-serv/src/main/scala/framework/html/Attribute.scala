/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

final class Attribute private[html] (val name: String, val value: String)

final class AttributeName private[html] (val name: String):
  def :=(value: String): Attribute = Attribute.create(name, value)

object Attribute:
  private val ValidName = "[A-Za-z_:][A-Za-z0-9_.:-]*".r
  private val UrlAttributes = Set("href", "src", "action", "formaction", "cite", "poster")
  private val AllowedUrlSchemes = Set("http", "https", "mailto", "tel")

  def name(value: String): AttributeName =
    validateName(value)
    new AttributeName(value.toLowerCase(java.util.Locale.ROOT))

  private[html] def create(name: String, value: String): Attribute =
    validateName(name)
    require(value != null, "Attribute values cannot be null")
    val normalizedName = name.toLowerCase(java.util.Locale.ROOT)
    if UrlAttributes.contains(normalizedName) then validateUrl(value)
    new Attribute(normalizedName, value)

  private[html] def validateName(name: String): Unit =
    require(name != null && ValidName.matches(name), "Invalid HTML attribute name")
    require(!name.toLowerCase(java.util.Locale.ROOT).startsWith("on"),
      "Event-handler attributes are not allowed")

  private def validateUrl(value: String): Unit =
    val normalized = value.trim
    require(!normalized.exists(_.isControl), "Control characters are not allowed in URLs")
    val scheme = "(?i)^([a-z][a-z0-9+.-]*):".r
    scheme.findFirstMatchIn(normalized).foreach { found =>
      require(AllowedUrlSchemes.contains(found.group(1).toLowerCase(java.util.Locale.ROOT)),
        "Unsafe URL scheme")
    }

object Attributes:
  val id = Attribute.name("id")
  val cls = Attribute.name("class")
  val href = Attribute.name("href")
  val src = Attribute.name("src")
  val alt = Attribute.name("alt")
  val title = Attribute.name("title")
  val lang = Attribute.name("lang")
  val charset = Attribute.name("charset")
  val name = Attribute.name("name")
  val content = Attribute.name("content")
  val `type` = Attribute.name("type")
  val action = Attribute.name("action")
  val value = Attribute.name("value")
  val role = Attribute.name("role")
  val rel = Attribute.name("rel")
  val target = Attribute.name("target")
  val xmlns = Attribute.name("xmlns")
  val viewport = Attribute.name("viewport")
