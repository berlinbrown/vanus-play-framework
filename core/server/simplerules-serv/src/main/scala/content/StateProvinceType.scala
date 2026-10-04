/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final class StateProvinceType private (
    val country: CountryCodeType,
    val code: String,
    name: String
) extends ContentTextType(name, StateProvinceType.NameLimit):
  val isoCode: String = s"${country.data}-$code"

object StateProvinceType:
  val NameLimit = 100

  def from(country: CountryCodeType, code: String): Option[StateProvinceType] =
    if country == null || code == null || !code.matches("[A-Z0-9]{1,3}") then None
    else StateProvinceRegistry.find(country, code)
      .map(name => new StateProvinceType(country, code, name))
