/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

abstract class SimpleRuleBaseType(
    val data: String,
    val limit: Int,
    val rules: Set[String]
):
  require(data != null, "Data cannot be null")
  require(limit > 0, "Limit must be positive")
  require(data.length <= limit, s"Text exceeds limit of $limit characters")
  require(rules != null, "Rules cannot be null")
  require(rules.forall(rule => rule != null && rule.nonEmpty), "Rule names cannot be null or empty")

  final val hash: String = SimpleRuleBaseType.createHash(data)

object SimpleRuleBaseType:
  private[content] def createHash(data: String): String =
    val digest = MessageDigest.getInstance("SHA-256")
      .digest(data.getBytes(StandardCharsets.UTF_8))
    Base64.getUrlEncoder.withoutPadding().encodeToString(digest)
