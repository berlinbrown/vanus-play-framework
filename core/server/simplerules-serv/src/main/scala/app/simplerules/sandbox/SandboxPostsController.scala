/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.sandbox

import app.bootstrap.sandbox.SandboxContent
import app.simplerules.sandbox.repository.InMemoryContentPostRepository
import app.simplerules.sandbox.view.SandboxPostsView
import content.service.ContentPostService
import framework.mvc.{Controller, RequestContext, ViewResult}
import org.nanohttpd.protocols.http.response.Status

final class SandboxPostsController(
    page: SandboxPostsPage = SandboxPostsController.defaultPage
) extends Controller:
  override def handle(context: RequestContext): ViewResult =
    ViewResult(Status.OK, new SandboxPostsView(page.load()))

object SandboxPostsController:
  val Path = SandboxPostsPage.Path

  private def defaultPage: SandboxPostsPage =
    val repository = new InMemoryContentPostRepository(SandboxContent.PublicPosts)
    new SandboxPostsPage(new ContentPostService(repository))
