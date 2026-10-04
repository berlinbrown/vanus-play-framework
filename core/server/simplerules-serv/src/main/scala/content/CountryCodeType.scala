/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.util.Locale

final class CountryCodeType(data: String)
    extends AlphaNumericTextType(data, CountryCodeType.Length):
  require(data.matches("[A-Z]{2}"), "Country code must contain exactly two uppercase letters")
  require(CountryCodeType.isSupported(data), s"Unsupported ISO country code: $data")

object CountryCodeType:
  val Length = 2

  final case class SupportedCountry(code: String, name: String)

  val supportedCountries: Vector[SupportedCountry] =
    Locale.getISOCountries.toVector
      .map { code =>
        val locale = new Locale.Builder().setRegion(code).build()
        SupportedCountry(code, locale.getDisplayCountry(Locale.ENGLISH))
      }
      .sortBy(_.name)

  val supportedCodes: Set[String] = supportedCountries.iterator.map(_.code).toSet

  def isSupported(code: String): Boolean = code != null && supportedCodes.contains(code)

