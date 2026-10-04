/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final class PostalCodeType(data: String)
    extends ContentTextType(data, PostalCodeType.Limit)

object PostalCodeType:
  val Limit = 20
