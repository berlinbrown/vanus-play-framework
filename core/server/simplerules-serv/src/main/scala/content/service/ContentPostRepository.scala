/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content.service

import content.ContentPost

trait ContentPostRepository:
  def findRecent(limit: Int, offset: Int): Vector[ContentPost]
  def countAll(): Long
  def findByGroup(groupId: Long, limit: Int, offset: Int): Vector[ContentPost]
  def findByCreator(userId: Long, limit: Int, offset: Int): Vector[ContentPost]
  def findByQuickLink(id: String): Option[ContentPost]
