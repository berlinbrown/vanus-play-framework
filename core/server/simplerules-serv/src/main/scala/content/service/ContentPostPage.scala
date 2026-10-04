/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content.service

import content.ContentPost

final case class ContentPostPage(
    posts: Vector[ContentPost],
    totalCount: Long,
    pageNumber: Int,
    pageSize: Int
)
