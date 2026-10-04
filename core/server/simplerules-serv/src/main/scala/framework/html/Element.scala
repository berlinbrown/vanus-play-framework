/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.html

final class Tag private[html] (name: String):
  def apply(text: String): Html = Element.create(name, Nil, Vector(Text(text)))
  def apply(children: Html*): Html = Element.create(name, Nil, children)
  def apply(children: IterableOnce[Html]): Html = Element.create(name, Nil, children.iterator.toVector)
  def apply(attributes: Attribute*)(text: String): Html = Element.create(name, attributes, Vector(Text(text)))
  def apply(attributes: Attribute*)(children: Html*): Html = Element.create(name, attributes, children)
  def apply(attributes: Attribute*)(children: IterableOnce[Html]): Html =
    Element.create(name, attributes, children.iterator.toVector)

final class VoidTag private[html] (name: String):
  def apply(attributes: Attribute*): Html = Element.create(name, attributes, Nil)
