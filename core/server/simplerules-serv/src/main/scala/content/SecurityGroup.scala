/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.time.Instant

final case class SecurityGroup(
    groupId: Long,
    resourceId: PermalinkPathType,
    groupName: AlphaNumericContentTextType,
    description: ContentTextType,
    owner: User,
    members: Set[User],
    createdTimestamp: Instant,
    updatedTimestamp: Instant
)
