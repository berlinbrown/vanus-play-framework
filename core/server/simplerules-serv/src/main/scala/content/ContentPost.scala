/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.time.Instant

final case class ContentPost(
    title: AlphaNumericContentTextType,
    text: Option[ContentTextType],
    permalink: AlphaNumericTextType,
    quickLink: PermalinkPathType,
    url: Option[UrlType],
    contentType: ContentPostType,
    createdTimestamp: Instant,
    updatedTimestamp: Instant,
    creator: User,
    owner: User,
    group: Option[SecurityGroup]
) extends SimpleRuleTimestamped:
  require(title.data.length <= ContentPost.TitleLimit,
    s"Title exceeds limit of ${ContentPost.TitleLimit} characters")
  require(text.forall(_.data.length <= ContentPost.TextLimit),
    s"Text exceeds limit of ${ContentPost.TextLimit} characters")
  require(ContentPost.hasValidContent(contentType, text, url),
    s"Text and URL do not match content type $contentType")

object ContentPost:
  val TitleLimit = 200
  val TextLimit = 2048

  private def hasValidContent(
      contentType: ContentPostType,
      text: Option[ContentTextType],
      url: Option[UrlType]
  ): Boolean = contentType match
    case ContentPostType.LinkOnly => text.isEmpty && url.nonEmpty
    case ContentPostType.TextOnly => text.nonEmpty && url.isEmpty
    case ContentPostType.LinkText => text.nonEmpty && url.nonEmpty
