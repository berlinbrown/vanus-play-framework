/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

final case class User(
    username: UsernameType,
    longName: ContentTextType,
    bannerMessage: ContentTextType,
    userId: Long,
    description: ContentTextType,
    profilePath: PermalinkPathType,
    resourceId: PermalinkPathType,
    location: UserLocation
)
