/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path

class VanusXhtmlValidatorSuite extends munit.FunSuite:
  test("validates well-formed markup with an HTML5 doctype") {
    VanusXhtmlValidator.validate("<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><body><br /></body></html>")
  }

  test("rejects an HTML doctype whose case is invalid in XML") {
    intercept[VanusMarkupException](
      VanusXhtmlValidator.validate("<!doctype html><html xmlns=\"http://www.w3.org/1999/xhtml\"><body></body></html>")
    )
  }

  test("the default document-root index is valid XHTML") {
    VanusXhtmlValidator.validate(Files.readAllBytes(Path.of("index.html")))
  }

  test("rejects XML markup without the XHTML namespace") {
    intercept[VanusMarkupException](VanusXhtmlValidator.validate("<html><body>Home</body></html>"))
  }

  test("throws a framework-specific exception for malformed markup") {
    val error = intercept[VanusMarkupException](VanusXhtmlValidator.validate(
      "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><p>broken</body></html>"))
    assertEquals(error.getMessage, "Invalid XHTML markup")
    assert(error.getCause != null)
  }

  test("rejects DTD and external entity declarations") {
    val markup = "<!DOCTYPE html [<!ENTITY x SYSTEM 'file:///etc/passwd'>]><html xmlns='http://www.w3.org/1999/xhtml'>&x;</html>"
    intercept[VanusMarkupException](VanusXhtmlValidator.validate(new ByteArrayInputStream(markup.getBytes("UTF-8"))))
  }
