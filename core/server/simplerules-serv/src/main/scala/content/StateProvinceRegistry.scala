/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.nio.charset.StandardCharsets
import scala.io.Source

object StateProvinceRegistry:
  private val ResourcePath = "content/states.csv"
  private val CodePattern = "[A-Z0-9]{1,3}".r

  private val states: Map[(String, String), String] = loadFromResources(ResourcePath)

  def find(country: CountryCodeType, code: String): Option[String] =
    if country == null || code == null then None else states.get((country.data, code))

  def supportedStates: Vector[(String, String, String)] =
    states.iterator.map { case ((country, code), name) => (country, code, name) }
      .toVector.sortBy(entry => (entry._1, entry._3))

  private def loadFromResources(path: String): Map[(String, String), String] =
    val stream = Option(getClass.getClassLoader.getResourceAsStream(path))
      .getOrElse(throw new IllegalStateException(s"Missing state/province resource: $path"))
    val source = Source.fromInputStream(stream, StandardCharsets.UTF_8.name())
    try
      val lines = source.getLines().toVector
      require(lines.headOption.contains("country,code,name"),
        s"Invalid state/province CSV header in $path")
      val entries = lines.drop(1).zipWithIndex.collect {
        case (line, _) if line.trim.isEmpty || line.trim.startsWith("#") => None
        case (line, index) =>
          val columns = line.split(",", 3).map(_.trim)
          require(columns.length == 3, s"Invalid state/province CSV row ${index + 2}")
          val Array(country, code, name) = columns: @unchecked
          require(CountryCodeType.isSupported(country),
            s"Unsupported country '$country' on CSV row ${index + 2}")
          require(CodePattern.matches(code), s"Invalid subdivision code '$code' on CSV row ${index + 2}")
          require(name.nonEmpty && name.length <= StateProvinceType.NameLimit,
            s"Invalid subdivision name on CSV row ${index + 2}")
          Some((country, code) -> name)
      }.flatten
      require(entries.map(_._1).distinct.size == entries.size,
        s"Duplicate state/province code in $path")
      entries.toMap
    finally source.close()

