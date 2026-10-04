/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.time.Instant

class SimpleRuleBaseTypeSuite extends munit.FunSuite:
  test("creates a valid immutable alphanumeric value") {
    val value = new AlphaNumericTextType("MyBlog123", 100)

    assertEquals(value.data, "MyBlog123")
    assertEquals(value.limit, 100)
    assertEquals(value.rules, Set("alphanumeric"))
    assertEquals(value.hash, "jQUMxmuNBDalqHZIrGFgcF9DEA3v9KI90VZ7H-lwqQA")
  }

  test("equal text produces the same URL-safe SHA-256 hash") {
    val first = new AlphaNumericTextType("SameText", 20)
    val second = new AlphaNumericTextType("SameText", 100)

    assertEquals(first.hash, second.hash)
    assert(first.hash.matches("[A-Za-z0-9_-]{43}"))
  }

  test("rejects text over its limit") {
    val error = intercept[IllegalArgumentException](new AlphaNumericTextType("abcd", 3))
    assert(error.getMessage.contains("Text exceeds limit"))
  }

  test("rejects punctuation, whitespace, and empty text") {
    for invalid <- Seq("My-Blog!", "two words", "") do
      intercept[IllegalArgumentException](new AlphaNumericTextType(invalid, 100))
  }

  test("rejects a non-positive limit") {
    intercept[IllegalArgumentException](new AlphaNumericTextType("valid", 0))
  }

  test("generates a validated random permalink path") {
    val permalink = new PermalinkPathType()

    assertEquals(permalink.data.length, PermalinkPathType.Length)
    assertEquals(permalink.limit, PermalinkPathType.Length)
    assertEquals(permalink.rules, Set(AlphaNumericTextType.Rule))
    assert(permalink.data.matches("[A-Za-z0-9]{21}"))
    assert(permalink.hash.matches("[A-Za-z0-9_-]{43}"))
  }

  test("creates a supported ISO country code") {
    val country = new CountryCodeType("US")

    assertEquals(country.data, "US")
    assertEquals(country.limit, 2)
    assert(CountryCodeType.isSupported("US"))
    assert(CountryCodeType.supportedCountries.exists(country =>
      country.code == "US" && country.name == "United States"))
  }

  test("loads the complete ISO alpha-2 country collection") {
    assertEquals(CountryCodeType.supportedCountries.size, 249)
    assertEquals(CountryCodeType.supportedCodes.size, 249)
    assert(CountryCodeType.supportedCountries.forall(country => country.code.matches("[A-Z]{2}")))
    assertEquals(CountryCodeType.supportedCountries.map(_.name),
      CountryCodeType.supportedCountries.map(_.name).sorted)
  }

  test("rejects lowercase, malformed, and unsupported country codes") {
    for invalid <- Seq("us", "USA", "U1", "ZZ") do
      intercept[IllegalArgumentException](new CountryCodeType(invalid))
  }

  test("loads a country-aware state or province from the resource registry") {
    val georgia = StateProvinceType.from(new CountryCodeType("US"), "GA").get
    val ontario = StateProvinceType.from(new CountryCodeType("CA"), "ON").get

    assertEquals(georgia.data, "Georgia")
    assertEquals(georgia.code, "GA")
    assertEquals(georgia.isoCode, "US-GA")
    assertEquals(georgia.rules, Set(ContentTextType.Rule))
    assertEquals(ontario.data, "Ontario")
    assertEquals(ontario.isoCode, "CA-ON")
  }

  test("supports Unicode subdivision names loaded from CSV") {
    val nuevoLeon = StateProvinceType.from(new CountryCodeType("MX"), "NLE").get

    assertEquals(nuevoLeon.data, "Nuevo León")
    assertEquals(nuevoLeon.isoCode, "MX-NLE")
  }

  test("state and province lookup rejects unknown or malformed combinations") {
    assertEquals(StateProvinceType.from(new CountryCodeType("US"), "ON"), None)
    assertEquals(StateProvinceType.from(new CountryCodeType("US"), "ga"), None)
    assertEquals(StateProvinceType.from(new CountryCodeType("US"), "TOOLONG"), None)
  }

  test("exposes the immutable supported subdivision collection") {
    val states = StateProvinceRegistry.supportedStates

    assertEquals(states.size, 11)
    assert(states.contains(("GB", "SCT", "Scotland")))
    assert(states.contains(("MX", "CMX", "Ciudad de México")))
  }

  test("creates postal codes without enumerating country-specific values") {
    val canadian = new PostalCodeType("K1A 0B1")
    val american = new PostalCodeType("30301")

    assertEquals(canadian.data, "K1A 0B1")
    assertEquals(american.data, "30301")
    assertEquals(canadian.rules, Set(ContentTextType.Rule))
  }

  test("creates a county or region containing spaces") {
    val county = new CountyRegionType("Los Angeles County")

    assertEquals(county.data, "Los Angeles County")
    assertEquals(county.rules, Set(ContentTextType.Rule))
  }

  test("creates an alphanumeric city name") {
    val city = new CityNameType("Atlanta")

    assertEquals(city.data, "Atlanta")
    assertEquals(city.rules, Set(AlphaNumericTextType.Rule))
    intercept[IllegalArgumentException](new CityNameType("New York"))
  }

  test("composes validated values into a user location") {
    val country = new CountryCodeType("US")
    val state = StateProvinceType.from(country, "GA").get
    val county = new CountyRegionType("Fulton County")
    val city = new CityNameType("Atlanta")
    val postalCode = new PostalCodeType("30303")
    val location = UserLocation(country, state, Some(county), city, Some(postalCode))

    assertEquals(location.country, country)
    assertEquals(location.stateProvince, state)
    assertEquals(location.county, Some(county))
    assertEquals(location.city, city)
    assertEquals(location.postalCode, Some(postalCode))
  }

  test("creates a bounded alphanumeric username with a case-insensitive uniqueness key") {
    val mixedCase = new UsernameType("BerlinBrown")
    val lowercase = new UsernameType("berlinbrown")

    assertEquals(mixedCase.data, "BerlinBrown")
    assertEquals(mixedCase.limit, UsernameType.MaxLength)
    assertEquals(mixedCase.rules, Set(AlphaNumericTextType.Rule))
    assertEquals(mixedCase.uniquenessKey, lowercase.uniquenessKey)
  }

  test("rejects usernames outside their length bounds or containing non-alphanumeric characters") {
    intercept[IllegalArgumentException](new UsernameType("ab"))
    intercept[IllegalArgumentException](new UsernameType("a" * (UsernameType.MaxLength + 1)))
    intercept[IllegalArgumentException](new UsernameType("berlin_brown"))
  }

  test("composes validated content into a user") {
    val country = new CountryCodeType("US")
    val location = UserLocation(
      country,
      StateProvinceType.from(country, "GA").get,
      Some(new CountyRegionType("Fulton County")),
      new CityNameType("Atlanta"),
      Some(new PostalCodeType("30303"))
    )
    val profilePath = new PermalinkPathType()
    val resourceId = new PermalinkPathType()
    val user = User(
      username = new UsernameType("BerlinBrown"),
      longName = new ContentTextType("Berlin Brown", 100),
      bannerMessage = new ContentTextType("Welcome to my profile", 200),
      userId = 42L,
      description = new ContentTextType("Software developer and writer", 500),
      profilePath = profilePath,
      resourceId = resourceId,
      location = location
    )

    assertEquals(user.username.data, "BerlinBrown")
    assertEquals(user.longName.data, "Berlin Brown")
    assertEquals(user.userId, 42L)
    assertEquals(user.profilePath, profilePath)
    assertEquals(user.resourceId, resourceId)
    assertEquals(user.location, location)
  }

  test("creates an alphanumeric content value with spaces") {
    val value = new AlphaNumericContentTextType("Project Owners 2", 100)

    assertEquals(value.data, "Project Owners 2")
    assertEquals(value.rules, Set(AlphaNumericContentTextType.Rule))
    intercept[IllegalArgumentException](
      new AlphaNumericContentTextType("Project Owners!", 100))
  }

  test("composes users and timestamps into a security group") {
    val country = new CountryCodeType("US")
    val location = UserLocation(
      country,
      StateProvinceType.from(country, "GA").get,
      None,
      new CityNameType("Atlanta"),
      None
    )
    val owner = User(
      new UsernameType("GroupOwner"),
      new ContentTextType("Group Owner", 100),
      new ContentTextType("Welcome", 200),
      1L,
      new ContentTextType("Security group owner", 500),
      new PermalinkPathType(),
      new PermalinkPathType(),
      location
    )
    val created = Instant.parse("2026-10-04T12:00:00Z")
    val updated = Instant.parse("2026-10-04T13:00:00Z")
    val group = SecurityGroup(
      10L,
      new PermalinkPathType(),
      new AlphaNumericContentTextType("Project Owners", 100),
      new ContentTextType("Users who own this project", 500),
      owner,
      Set(owner),
      created,
      updated
    )

    assertEquals(group.groupName.data, "Project Owners")
    assertEquals(group.owner, owner)
    assertEquals(group.members, Set(owner))
    assertEquals(group.createdTimestamp, created)
    assertEquals(group.updatedTimestamp, updated)
  }

  test("creates valid HTTP and HTTPS URLs") {
    val http = new UrlType("http://example.com")
    val https = new UrlType("https://example.com/users/BerlinBrown?tab=profile")

    assertEquals(http.data, "http://example.com")
    assertEquals(https.limit, 2048)
    assertEquals(https.rules, Set("valid-url"))
  }

  test("rejects empty, hostless, unsupported, malformed, and oversized URLs") {
    for invalid <- Seq(
      "",
      "/users/BerlinBrown",
      "https:///missing-host",
      "ftp://example.com/resource",
      "not a url"
    ) do intercept[IllegalArgumentException](new UrlType(invalid))

    intercept[IllegalArgumentException](
      new UrlType("https://example.com/" + ("a" * 2040)))
  }

  test("defines the supported content post types") {
    assertEquals(
      ContentPostType.values.toSeq,
      Seq(ContentPostType.LinkOnly, ContentPostType.TextOnly, ContentPostType.LinkText)
    )
  }

  test("creates timestamped content posts with matching content") {
    val country = new CountryCodeType("US")
    val location = UserLocation(
      country,
      StateProvinceType.from(country, "GA").get,
      None,
      new CityNameType("Atlanta"),
      None
    )
    val creator = User(
      new UsernameType("PostCreator"),
      new ContentTextType("Post Creator", 100),
      new ContentTextType("Welcome", 200),
      2L,
      new ContentTextType("Creates posts", 500),
      new PermalinkPathType(),
      new PermalinkPathType(),
      location
    )
    val created = Instant.parse("2026-10-04T12:00:00Z")
    val updated = Instant.parse("2026-10-04T13:00:00Z")
    val post = ContentPost(
      new AlphaNumericContentTextType("Example Post", ContentPost.TitleLimit),
      Some(new ContentTextType("Post text", ContentPost.TextLimit)),
      new AlphaNumericTextType("ExamplePost", ContentPost.TitleLimit),
      new PermalinkPathType(),
      Some(new UrlType("https://example.com/post")),
      ContentPostType.LinkText,
      created,
      updated,
      creator,
      creator,
      None
    )

    assert(post.isInstanceOf[SimpleRuleTimestamped])
    assertEquals(post.createdTimestamp, created)
    assertEquals(post.creator, creator)
    assertEquals(post.owner, creator)
  }

  test("rejects content posts whose optional content does not match their type") {
    val country = new CountryCodeType("US")
    val location = UserLocation(
      country,
      StateProvinceType.from(country, "GA").get,
      None,
      new CityNameType("Atlanta"),
      None
    )
    val user = User(
      new UsernameType("PostOwner"),
      new ContentTextType("Post Owner", 100),
      new ContentTextType("Welcome", 200),
      3L,
      new ContentTextType("Owns posts", 500),
      new PermalinkPathType(),
      new PermalinkPathType(),
      location
    )

    intercept[IllegalArgumentException](ContentPost(
      new AlphaNumericContentTextType("Invalid Post", ContentPost.TitleLimit),
      None,
      new AlphaNumericTextType("InvalidPost", ContentPost.TitleLimit),
      new PermalinkPathType(),
      None,
      ContentPostType.LinkOnly,
      Instant.EPOCH,
      Instant.EPOCH,
      user,
      user,
      None
    ))
  }
