/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.security.SecureRandom

object PermalinkPathType:
  val Length = 21

  private val Random = new SecureRandom()
  private val Alphabet =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
      "abcdefghijklmnopqrstuvwxyz" +
      "0123456789"

  private def generate(): String =
    (1 to Length)
      .map(_ => Alphabet.charAt(Random.nextInt(Alphabet.length)))
      .mkString

final class PermalinkPathType()
    extends AlphaNumericTextType(PermalinkPathType.generate(), PermalinkPathType.Length)
