/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

class AlphaNumericTextType(data: String, limit: Int)
    extends SimpleRuleBaseType(data, limit, Set(AlphaNumericTextType.Rule)):
  require(AlphaNumericTextType.Pattern.matches(data), "Only alphanumeric characters are allowed")

object AlphaNumericTextType:
  val Rule = "alphanumeric"
  private val Pattern = "[a-zA-Z0-9]+".r
