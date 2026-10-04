/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package framework.mvc

import framework.html.HtmlRenderer
import org.nanohttpd.protocols.http.response.Response

object ResponseAdapter:
  def toNanoHttpd(result: ViewResult): Response =
    val response = Response.newFixedLengthResponse(
      result.status,
      result.contentType,
      HtmlRenderer.renderDocument(result.view.render)
    )
    result.headers.foreach { case (name, value) => response.addHeader(name, value) }
    response
