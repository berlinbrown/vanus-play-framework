/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.mvc

import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.response.IStatus

import scala.jdk.CollectionConverters.*

final case class RequestContext(path: String, method: String, headers: Map[String, String])

object RequestContext:
  def from(session: IHTTPSession): RequestContext =
    RequestContext(session.getUri, session.getMethod.toString,
      session.getHeaders.asScala.iterator.map { case (key, value) => key.toLowerCase(java.util.Locale.ROOT) -> value }.toMap)

trait Controller:
  def handle(context: RequestContext): ViewResult

final case class ViewResult(
    status: IStatus,
    view: View,
    headers: Map[String, String] = Map.empty,
    contentType: String = "text/html; charset=utf-8"
)
