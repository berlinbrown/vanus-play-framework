/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

object VanusRequestLog:
  def format(
      method: String,
      uri: String,
      status: Int,
      elapsedMillis: Long,
      ipAddress: String,
      userAgent: String
  ): String =
    s"method=${field(method, 16)} uri=${quoted(uri, 2048)} status=$status " +
      s"ip=${quoted(ipAddress, 64)} user_agent=${quoted(userAgent, 512)} " +
      s"handler_ms=$elapsedMillis"

  private def quoted(value: String, maxLength: Int): String =
    s"\"${field(value, maxLength)}\""

  private def field(value: String, maxLength: Int): String =
    Option(value).filter(_.nonEmpty).getOrElse("unknown")
      .take(maxLength)
      .flatMap {
        case '\\' => "\\\\"
        case '"' => "\\\""
        case character if character.isControl => "?"
        case character => character.toString
      }
