package app.simplerules.sandbox

import app.simplerules.sandbox.model.SandboxPostsPageModel
import content.service.ContentPostService

final class SandboxPostsPage(contentPostService: ContentPostService):
  require(contentPostService != null, "Content post service cannot be null")

  def load(page: Int = 1): SandboxPostsPageModel =
    SandboxPostsPageModel(
      title = "Sandbox Posts",
      content = contentPostService.recentPosts(page, SandboxPostsPage.DefaultPageSize)
    )

object SandboxPostsPage:
  val DefaultPageSize = 20
  val Path = "/simplerules/app/sandbox-content-examples.van"
