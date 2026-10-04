/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

class VanusRequestLogSuite extends munit.FunSuite:
  test("formats useful request context") {
    val message = VanusRequestLog.format(
      "GET",
      "/simplerules/app/sandbox-content-examples.van",
      200,
      49L,
      "127.0.0.1",
      "Mozilla/5.0"
    )

    assertEquals(message,
      "method=GET uri=\"/simplerules/app/sandbox-content-examples.van\" status=200 " +
        "ip=\"127.0.0.1\" user_agent=\"Mozilla/5.0\" handler_ms=49")
  }

  test("prevents untrusted values from injecting log lines") {
    val message = VanusRequestLog.format(
      "GET",
      "/safe\nforged=true",
      200,
      1L,
      "127.0.0.1\radmin=true",
      "browser \"quoted\"\\value\nforged=true"
    )

    assert(!message.contains('\n'))
    assert(!message.contains('\r'))
    assert(message.contains("uri=\"/safe?forged=true\""))
    assert(message.contains("user_agent=\"browser \\\"quoted\\\"\\\\value?forged=true\""))
  }
