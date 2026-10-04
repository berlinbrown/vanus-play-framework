/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content.service

final class ContentPostService(
    repository: ContentPostRepository
):
  require(repository != null, "Content post repository cannot be null")

  def recentPosts(page: Int, pageSize: Int): ContentPostPage =
    val safePage = page.max(1)
    val safeSize = pageSize.max(1).min(ContentPostService.MaxPageSize)
    val offset = (safePage.toLong - 1) * safeSize

    require(offset <= Int.MaxValue, "Page number too large")

    ContentPostPage(
      posts = repository.findRecent(safeSize, offset.toInt),
      totalCount = repository.countAll(),
      pageNumber = safePage,
      pageSize = safeSize
    )

object ContentPostService:
  val MaxPageSize = 50
