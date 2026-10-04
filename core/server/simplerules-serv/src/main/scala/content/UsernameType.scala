/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.util.Locale

final class UsernameType(data: String)
    extends AlphaNumericTextType(data, UsernameType.MaxLength):
  require(data.length >= UsernameType.MinLength,
    s"Username must contain at least ${UsernameType.MinLength} characters")

  /** Store this value in a uniquely constrained database column. */
  val uniquenessKey: String = data.toLowerCase(Locale.ROOT)

object UsernameType:
  val MinLength = 3
  val MaxLength = 30
