/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content.service

import content.ContentPost

class ContentPostServiceSuite extends munit.FunSuite:
  private final class StubRepository extends ContentPostRepository:
    var recentRequest: Option[(Int, Int)] = None

    override def findRecent(limit: Int, offset: Int): Vector[ContentPost] =
      recentRequest = Some((limit, offset))
      Vector.empty

    override def countAll(): Long = 125L

    override def findByGroup(
        groupId: Long,
        limit: Int,
        offset: Int
    ): Vector[ContentPost] = Vector.empty

    override def findByCreator(
        userId: Long,
        limit: Int,
        offset: Int
    ): Vector[ContentPost] = Vector.empty

    override def findByQuickLink(id: String): Option[ContentPost] = None

  test("loads a requested page of recent posts") {
    val repository = new StubRepository()
    val service = new ContentPostService(repository)

    val result = service.recentPosts(3, 20)

    assertEquals(repository.recentRequest, Some((20, 40)))
    assertEquals(result.posts, Vector.empty)
    assertEquals(result.totalCount, 125L)
    assertEquals(result.pageNumber, 3)
    assertEquals(result.pageSize, 20)
  }

  test("clamps page and page size to safe values") {
    val repository = new StubRepository()
    val service = new ContentPostService(repository)

    val minimum = service.recentPosts(0, 0)
    assertEquals(repository.recentRequest, Some((1, 0)))
    assertEquals(minimum.pageNumber, 1)
    assertEquals(minimum.pageSize, 1)

    val maximum = service.recentPosts(2, 1000)
    assertEquals(repository.recentRequest, Some((ContentPostService.MaxPageSize, 50)))
    assertEquals(maximum.pageSize, ContentPostService.MaxPageSize)
  }

  test("rejects a page whose database offset cannot fit in an Int") {
    val service = new ContentPostService(new StubRepository())

    intercept[IllegalArgumentException](service.recentPosts(Int.MaxValue, 50))
  }

  test("builds the home page model from recent posts") {
    val repository = new StubRepository()
    val handler = new HomePageHandler(new ContentPostService(repository))

    val model = handler.loadPage()

    assertEquals(model.title, "Simple Rules")
    assertEquals(model.recentPosts.pageNumber, 1)
    assertEquals(model.recentPosts.pageSize, 20)
    assertEquals(repository.recentRequest, Some((20, 0)))
  }
