package app.simplerules.sandbox

import app.bootstrap.sandbox.SandboxContent
import app.simplerules.sandbox.repository.InMemoryContentPostRepository
import app.simplerules.sandbox.view.SandboxPostsView
import content.service.ContentPostService
import framework.html.HtmlRenderer
import framework.mvc.{RequestContext, ResponseAdapter}
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource
import org.nanohttpd.protocols.http.response.Status

class SandboxPostsPageSuite extends munit.FunSuite:
  test("bootstrap creates MyBerlin as a member and owner of the admin group") {
    assertEquals(SandboxContent.MyBerlin.username.data, "MyBerlin")
    assertEquals(SandboxContent.AdminGroup.groupName.data, "Admin Group")
    assertEquals(SandboxContent.AdminGroup.owner, SandboxContent.MyBerlin)
    assert(SandboxContent.AdminGroup.members.contains(SandboxContent.MyBerlin))
  }

  test("repository returns posts newest first") {
    val repository = new InMemoryContentPostRepository(SandboxContent.PublicPosts.reverse)

    val posts = repository.findRecent(20, 0)

    assertEquals(posts.take(2).map(_.title.data),
      Vector("Why Small Systems Endure", "A Walk Through The Content Model"))
  }

  test("service paginates the in-memory repository") {
    val repository = new InMemoryContentPostRepository(SandboxContent.PublicPosts)
    val page = new ContentPostService(repository).recentPosts(2, 1)

    assertEquals(page.posts.map(_.title.data), Vector("A Walk Through The Content Model"))
    assertEquals(page.totalCount, 8L)
    assertEquals(page.pageNumber, 2)
    assertEquals(page.pageSize, 1)
  }

  test("page constructs its model with the sandbox defaults") {
    val repository = new InMemoryContentPostRepository(SandboxContent.PublicPosts)
    val model = new SandboxPostsPage(new ContentPostService(repository)).load()

    assertEquals(model.title, "Sandbox Posts")
    assertEquals(model.content.pageNumber, 1)
    assertEquals(model.content.pageSize, SandboxPostsPage.DefaultPageSize)
    assertEquals(model.content.totalCount, 8L)
  }

  test("view handles empty collections") {
    val repository = new InMemoryContentPostRepository(Vector.empty)
    val model = new SandboxPostsPage(new ContentPostService(repository)).load()
    val output = HtmlRenderer.renderDocument(new SandboxPostsView(model).render)

    assert(output.contains("No posts available."))
  }

  test("view produces valid XHTML and escapes post text") {
    val repository = new InMemoryContentPostRepository(SandboxContent.PublicPosts)
    val model = new SandboxPostsPage(new ContentPostService(repository)).load()
    val output = HtmlRenderer.renderDocument(new SandboxPostsView(model).render)
    val factory = DocumentBuilderFactory.newInstance()
    factory.setNamespaceAware(true)

    val document = factory.newDocumentBuilder().parse(
      new InputSource(new StringReader(output.replaceFirst("<!DOCTYPE html>", ""))))

    assertEquals(document.getDocumentElement.getNamespaceURI, "http://www.w3.org/1999/xhtml")
    assert(output.contains("A small &amp; understandable Scala service"))
    assert(!output.contains("A small & understandable Scala service"))
    assert(output.contains("Berlin Brown"))
    assert(output.contains("LinkText"))
    assert(output.contains(
      "<a href=\"https://example.com/simple-rules\" target=\"_blank\" rel=\"noopener noreferrer\">Open linked resource</a>"))
    assert(output.contains(
      "<a href=\"https://docs.scala-lang.org/scala3/\" target=\"_blank\" rel=\"noopener noreferrer\">Open linked resource</a>"))
  }

  test("controller serves the sandbox posts page at its public path") {
    val controller = new SandboxPostsController()
    val result = controller.handle(RequestContext(SandboxPostsController.Path, "GET", Map.empty))
    val response = ResponseAdapter.toNanoHttpd(result)
    val output = new String(response.getData.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)

    assertEquals(SandboxPostsController.Path,
      "/simplerules/app/sandbox-content-examples.van")
    assertEquals(response.getStatus, Status.OK)
    assert(output.contains("Sandbox Posts"))
    assert(output.contains("Why Small Systems Endure"))
  }
