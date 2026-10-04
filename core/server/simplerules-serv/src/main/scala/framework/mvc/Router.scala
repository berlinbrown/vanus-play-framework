/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.mvc

import org.nanohttpd.protocols.http.request.Method

final class Router(routes: Map[(Method, String), Controller]):
  def controller(method: Method, path: String): Option[Controller] =
    val normalizedMethod = if method == Method.HEAD then Method.GET else method
    routes.get((method, path)).orElse(routes.get((normalizedMethod, path)))

object Router:
  def apply(routes: Map[(Method, String), Controller]): Router = new Router(routes)
