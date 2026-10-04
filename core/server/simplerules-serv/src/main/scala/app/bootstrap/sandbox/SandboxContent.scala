/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.bootstrap.sandbox

import content.*
import java.time.Instant

object SandboxContent:
  private val country = new CountryCodeType("US")
  private val location = UserLocation(
    country,
    StateProvinceType.from(country, "GA").get,
    Some(new CountyRegionType("Fulton County")),
    new CityNameType("Atlanta"),
    Some(new PostalCodeType("30303"))
  )

  val MyBerlin: User = User(
    username = new UsernameType("MyBerlin"),
    longName = new ContentTextType("Berlin Brown", 100),
    bannerMessage = new ContentTextType("Building Simple Rules", 200),
    userId = 1L,
    description = new ContentTextType("Simple Rules administrator", 2048),
    profilePath = new PermalinkPathType(),
    resourceId = new PermalinkPathType(),
    location = location
  )

  val AdminGroup: SecurityGroup = SecurityGroup(
    groupId = 1L,
    resourceId = new PermalinkPathType(),
    groupName = new AlphaNumericContentTextType("Admin Group", 100),
    description = new ContentTextType("Simple Rules administrators", 2048),
    owner = MyBerlin,
    members = Set(MyBerlin),
    createdTimestamp = Instant.parse("2026-01-01T00:00:00Z"),
    updatedTimestamp = Instant.parse("2026-01-01T00:00:00Z")
  )

  private def textPost(title: String, permalink: String, body: String, timestamp: String): ContentPost =
    post(title, permalink, Some(body), None, ContentPostType.TextOnly, timestamp)

  private def linkedPost(
      title: String,
      permalink: String,
      body: Option[String],
      url: String,
      timestamp: String
  ): ContentPost =
    val postType = if body.nonEmpty then ContentPostType.LinkText else ContentPostType.LinkOnly
    post(title, permalink, body, Some(url), postType, timestamp)

  private def post(
      title: String,
      permalink: String,
      body: Option[String],
      url: Option[String],
      postType: ContentPostType,
      timestamp: String
  ): ContentPost =
    val instant = Instant.parse(timestamp)
    ContentPost(
      new AlphaNumericContentTextType(title, ContentPost.TitleLimit),
      body.map(new ContentTextType(_, ContentPost.TextLimit)),
      new AlphaNumericTextType(permalink, ContentPost.TitleLimit),
      new PermalinkPathType(),
      url.map(new UrlType(_)),
      postType,
      instant,
      instant,
      MyBerlin,
      MyBerlin,
      Some(AdminGroup)
    )

  /** Public, read-only examples used only by the sandbox page. */
  val PublicPosts: Vector[ContentPost] = Vector(
    textPost(
      "Why Small Systems Endure",
      "WhySmallSystemsEndure",
      "The best software often begins with a few understandable pieces. Clear models, small services, and visible rules make a system easier to change with confidence.",
      "2026-10-10T14:30:00Z"
    ),
    textPost(
      "A Walk Through The Content Model",
      "WalkThroughContentModel",
      "Posts connect validated text, stable resource identifiers, authorship, ownership, and optional groups. Each field carries a small rule so the larger model stays predictable.",
      "2026-10-09T11:15:00Z"
    ),
    linkedPost(
      "Useful Scala Documentation",
      "UsefulScalaDocumentation",
      None,
      "https://docs.scala-lang.org/scala3/",
      "2026-10-08T16:00:00Z"
    ),
    linkedPost(
      "Designing For Readable Pages",
      "DesigningForReadablePages",
      Some("Server rendered XHTML can remain direct and pleasant. Semantic markup, restrained styles, and careful escaping provide a durable foundation."),
      "https://developer.mozilla.org/en-US/docs/Glossary/Semantics",
      "2026-10-07T09:45:00Z"
    ),
    textPost(
      "Community Notes For October",
      "CommunityNotesOctober",
      "This month we are focusing on readable content pages, replaceable repositories, and small view models that keep rendering independent from storage.",
      "2026-10-06T18:20:00Z"
    ),
    textPost(
      "Release Notes Zero One Two",
      "ReleaseNotesZeroOneTwo",
      "The sandbox now includes users, security groups, content types, pagination, and a public collection of example posts.",
      "2026-10-05T13:10:00Z"
    ),
    linkedPost(
      "Simple Rules Project",
      "SimpleRulesProject",
      Some("A small & understandable Scala service"),
      "https://example.com/simple-rules",
      "2026-10-04T14:00:00Z"
    ),
    textPost(
      "Welcome To The Sandbox",
      "WelcomeToTheSandbox",
      "This is a public sample text post and a safe place to exercise the content rendering pipeline.",
      "2026-10-03T14:00:00Z"
    )
  )
