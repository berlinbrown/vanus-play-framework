/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

sealed trait Html

final case class Text(value: String) extends Html

final case class Fragment(children: Vector[Html]) extends Html

final case class Element private[html] (
    name: String,
    attributes: Vector[Attribute],
    children: Vector[Html],
    isVoid: Boolean
) extends Html

object Element:
  private val ValidTagName = "[A-Za-z][A-Za-z0-9:-]*".r
  private val VoidNames = Set("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr")

  def create(name: String, attributes: Seq[Attribute], children: Seq[Html]): Html =
    require(name != null && ValidTagName.matches(name), "Invalid HTML element name")
    val normalizedName = name.toLowerCase(java.util.Locale.ROOT)
    val duplicateNames = attributes.groupBy(_.name).collect { case (key, values) if values.size > 1 => key }
    require(duplicateNames.isEmpty, "Duplicate HTML attributes are not allowed")
    attributes.foreach(attribute => Attribute.validateName(attribute.name))
    val isVoid = VoidNames.contains(normalizedName)
    require(!isVoid || children.isEmpty, s"Void HTML element <$normalizedName> cannot have children")
    Element(normalizedName, attributes.toVector, children.toVector, isVoid)

object Html:
  def text(value: String): Html = Text(value)
  def fragment(children: IterableOnce[Html]): Html = Fragment(children.iterator.toVector)
  val empty: Html = Fragment(Vector.empty)
