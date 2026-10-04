/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.mvc

import framework.html.Html

trait View:
  def render: Html

trait Layout:
  def wrap(content: Html): Html
