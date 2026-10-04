/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final case class UserLocation(
    country: CountryCodeType,
    stateProvince: StateProvinceType,
    county: Option[CountyRegionType],
    city: CityNameType,
    postalCode: Option[PostalCodeType]
)
