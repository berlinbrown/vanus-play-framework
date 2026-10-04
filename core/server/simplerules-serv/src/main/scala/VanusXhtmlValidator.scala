/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import java.io.{ByteArrayInputStream, File, InputStream}
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.{ErrorHandler, InputSource, SAXException, SAXParseException}
import org.xml.sax.helpers.DefaultHandler
import scala.util.control.NonFatal

final class VanusMarkupException(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)

object VanusXhtmlValidator:
  private val XhtmlNamespace = "http://www.w3.org/1999/xhtml"
  private val Html5Doctype = "(?s)^\\s*<!DOCTYPE\\s+html\\s*>".r
  private val Html5DoctypeIgnoringCase = "(?is)^\\s*<!doctype\\s+html\\s*>".r

  def validate(markup: String): Unit =
    try
      rejectInvalidDoctypeCase(markup)
      val withoutHtml5Doctype = Html5Doctype.replaceFirstIn(markup, "")
      parse(new InputSource(new java.io.StringReader(withoutHtml5Doctype)))
    catch
      case error: VanusMarkupException => throw error
      case NonFatal(error) => throw new VanusMarkupException("Invalid XHTML markup", error)

  def validate(input: InputStream): Unit =
    try parse(new InputSource(input))
    catch
      case error: VanusMarkupException => throw error
      case NonFatal(error) => throw new VanusMarkupException("Invalid XHTML markup", error)

  def validate(file: File): Unit =
    val input = java.nio.file.Files.newInputStream(file.toPath)
    try
      val bytes = input.readAllBytes()
      val sanitized = stripHtml5Doctype(bytes)
      validate(new ByteArrayInputStream(sanitized))
    finally input.close()

  def validate(responseBytes: Array[Byte]): Unit =
    validate(new ByteArrayInputStream(stripHtml5Doctype(responseBytes)))

  private def stripHtml5Doctype(bytes: Array[Byte]): Array[Byte] =
    val prefixLength = math.min(bytes.length, 512)
    val prefix = new String(bytes, 0, prefixLength, java.nio.charset.StandardCharsets.ISO_8859_1)
    rejectInvalidDoctypeCase(prefix)
    Html5Doctype.findPrefixOf(prefix) match
      case Some(doctype) => bytes.drop(doctype.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1).length)
      case None => bytes

  private def rejectInvalidDoctypeCase(markup: String): Unit =
    if Html5DoctypeIgnoringCase.findPrefixOf(markup).isDefined && Html5Doctype.findPrefixOf(markup).isEmpty then
      throw new VanusMarkupException("Invalid XHTML doctype; expected <!DOCTYPE html>")

  private def parse(source: InputSource): Unit =
    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(true)
    factory.setXIncludeAware(false)
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    val reader = factory.newSAXParser().getXMLReader
    reader.setFeature("http://xml.org/sax/features/external-general-entities", false)
    reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    reader.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "")
    reader.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
    reader.setContentHandler(new DefaultHandler:
      private var rootSeen = false

      override def startElement(uri: String, localName: String, qName: String,
          attributes: org.xml.sax.Attributes): Unit =
        if !rootSeen then
          rootSeen = true
          if uri != XhtmlNamespace then
            throw new SAXException("Root element must use the XHTML namespace")
    )
    reader.setErrorHandler(new ErrorHandler:
      override def warning(error: SAXParseException): Unit = throw error
      override def error(error: SAXParseException): Unit = throw error
      override def fatalError(error: SAXParseException): Unit = throw error
    )
    reader.parse(source)
