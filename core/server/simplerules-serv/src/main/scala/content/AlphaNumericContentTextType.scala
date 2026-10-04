/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

class AlphaNumericContentTextType(data: String, limit: Int)
    extends SimpleRuleBaseType(data, limit, Set(AlphaNumericContentTextType.Rule)):
  require(data.nonEmpty, "Alphanumeric content text cannot be empty")
  require(AlphaNumericContentTextType.Pattern.matches(data),
    "Only letters, numbers, and spaces are allowed")

object AlphaNumericContentTextType:
  val Rule = "alphanumeric-content-text"
  private val Pattern = "[\\p{L}\\p{N} ]+".r
