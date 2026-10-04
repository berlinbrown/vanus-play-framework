/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final class CountyRegionType(data: String)
    extends ContentTextType(data, CountyRegionType.Limit)

object CountyRegionType:
  val Limit = 100
