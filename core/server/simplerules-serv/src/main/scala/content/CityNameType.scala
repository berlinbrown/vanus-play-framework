/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final class CityNameType(data: String)
    extends AlphaNumericTextType(data, CityNameType.Limit)

object CityNameType:
  val Limit = 100
