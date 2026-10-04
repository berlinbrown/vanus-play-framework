/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

class ContentTextType(data: String, limit: Int)
    extends SimpleRuleBaseType(data, limit, Set(ContentTextType.Rule)):
  require(data.nonEmpty, "Content text cannot be empty")
  require(ContentTextType.Pattern.matches(data),
    "Content text contains unsupported characters")

object ContentTextType:
  val Rule = "content-text"
  private val Pattern = "[\\p{L}\\p{N} .,'’()&/-]+".r

