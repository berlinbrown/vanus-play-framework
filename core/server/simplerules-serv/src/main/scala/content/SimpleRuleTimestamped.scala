/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.time.Instant

trait SimpleRuleTimestamped:
  def createdTimestamp: Instant
  def updatedTimestamp: Instant
