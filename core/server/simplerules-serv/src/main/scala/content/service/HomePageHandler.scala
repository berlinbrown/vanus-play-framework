/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content.service

final class HomePageHandler(
    postService: ContentPostService
):
  require(postService != null, "Content post service cannot be null")

  def loadPage(): HomePageModel =
    HomePageModel(
      title = "Simple Rules",
      recentPosts = postService.recentPosts(1, 20)
    )
