/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.net.URI

final class UrlType(
    data: String
) extends SimpleRuleBaseType(
    data,
    2048,
    Set("valid-url")
):
  require(data.nonEmpty, "URL cannot be empty")

  private val uri = URI.create(data)

  require(
    Set("http", "https").contains(
      Option(uri.getScheme)
        .getOrElse("")
        .toLowerCase(java.util.Locale.ROOT)
    ),
    "Only HTTP and HTTPS URLs allowed"
  )

  require(
    uri.getHost != null,
    "URL must contain a valid host"
  )
