/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package app.simplerules.statushome.view

import app.simplerules.statushome.layout.StatusHomeLayout
import app.simplerules.statushome.model.{NodeState, NodeStatus, SystemStatus}
import framework.html.Html
import framework.html.HtmlDsl.*
import framework.mvc.View

final class StatusHomeView(status: SystemStatus) extends View:
  override def render: Html =
    val onlineNodes = status.nodes.count(_.state == NodeState.Online)
    new StatusHomeLayout("SimpleRules | Main Status").wrap(
      fragment(Vector(
        div(cls := "topbar")(Html.empty),
        header(cls := "masthead shell")(
          div(
            p(cls := "eyebrow")("SimpleRules / Operations"),
            p(cls := "brand")(status.serviceName)
          ),
          p(cls := "checked")(s"Last checked ${status.checkedAt}")
        ),
        main(cls := "shell")(
          section(cls := "overview")(
            article(cls := "panel main-status")(
              div(
                p(cls := "eyebrow")("Main Status"),
                h1(mainStatusHeading(status.state))
              ),
              p(cls := s"status ${stateClass(status.state)}")(status.state.label)
            ),
            article(cls := "panel")(
              p(cls := "metric")(s"$onlineNodes / ${status.nodes.size}"),
              p(cls := "metric-label")("Nodes online")
            )
          ),
          section(
            div(cls := "section-heading")(
              h2("Node status"),
              span(cls := "muted")(s"${status.nodes.size} nodes")
            ),
            div(cls := "table-wrap")(nodeTable(status.nodes))
          ),
          section(cls := "resources")(
            div(cls := "section-heading")(
              h2("Quick links"),
              span(cls := "muted")("Useful destinations")
            ),
            div(cls := "link-grid")(
              linkCard("Google", "Search the web", "https://www.google.com"),
              linkCard("SimpleRules status", "Reload this service status", "/simplerules/app/status.van"),
              linkCard("Scala", "Scala language documentation", "https://www.scala-lang.org"),
              linkCard("GitHub", "Browse software projects", "https://github.com"),
              linkCard("Wikipedia", "Reference and background reading", "https://www.wikipedia.org"),
              linkCard("Example", "A safe external link example", "https://example.com")
            )
          )
        ),
        footer(cls := "footer shell")(
          text("Status data is demonstration data supplied by the local StatusService.")
        )
      ))
    )

  private def linkCard(label: String, description: String, url: String): Html =
    article(cls := "panel link-card")(
      div(cls := "link-card-inner")(
        div(cls := "link-copy")(
          a(cls := "link-title", href := url)(label),
          p(cls := "link-description")(description)
        ),
        span(cls := "link-arrow")("→")
      )
    )

  private def nodeTable(nodes: Vector[NodeStatus]): Html =
    val rows = nodes.map { node =>
      tr(
        td(cls := "node-name")(node.name),
        td(p(cls := s"status ${stateClass(node.state)}")(node.state.label)),
        td(node.endpoint),
        td(node.version),
        td(node.uptime),
        td(node.responseMillis.fold("Unavailable")(millis => s"${millis} ms"))
      )
    }
    table(
      caption("Current health by node"),
      thead(
        tr(th("Node"), th("Status"), th("Endpoint"), th("Version"), th("Uptime"), th("Response"))
      ),
      tbody(if rows.nonEmpty then rows else Vector(tr(td("No nodes are configured."))))
    )

  private def stateClass(state: NodeState): String = state match
    case NodeState.Online => "state-online"
    case NodeState.Degraded => "state-degraded"
    case NodeState.Offline => "state-offline"

  private def mainStatusHeading(state: NodeState): String = state match
    case NodeState.Online => "Server Up"
    case NodeState.Degraded => "Server Degraded"
    case NodeState.Offline => "Server Down"
