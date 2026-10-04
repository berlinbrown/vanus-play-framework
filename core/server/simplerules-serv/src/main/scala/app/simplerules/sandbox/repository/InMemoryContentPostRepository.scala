/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.sandbox.repository

import content.ContentPost
import content.service.ContentPostRepository

final class InMemoryContentPostRepository(posts: Vector[ContentPost])
    extends ContentPostRepository:
  require(posts != null, "Posts cannot be null")

  private val recent = posts.sortWith((left, right) =>
    left.createdTimestamp.isAfter(right.createdTimestamp))

  override def findRecent(limit: Int, offset: Int): Vector[ContentPost] =
    paginate(recent, limit, offset)

  override def countAll(): Long = recent.size.toLong

  override def findByGroup(groupId: Long, limit: Int, offset: Int): Vector[ContentPost] =
    paginate(recent.filter(_.group.exists(_.groupId == groupId)), limit, offset)

  override def findByCreator(userId: Long, limit: Int, offset: Int): Vector[ContentPost] =
    paginate(recent.filter(_.creator.userId == userId), limit, offset)

  override def findByQuickLink(id: String): Option[ContentPost] =
    recent.find(_.quickLink.data == id)

  private def paginate(
      values: Vector[ContentPost],
      limit: Int,
      offset: Int
  ): Vector[ContentPost] =
    require(limit >= 0 && offset >= 0, "Limit and offset cannot be negative")
    val end = (offset.toLong + limit).min(Int.MaxValue).toInt
    values.slice(offset, end)
