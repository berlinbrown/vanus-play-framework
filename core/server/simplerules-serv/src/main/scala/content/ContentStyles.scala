/**
 * VanusPlayFramework-FileCopyrightText: 2026 - Berlin Brown <myberlinaustin _at_ proton me>
 * VanusPlayFramework-License-Identifier: Refer to LICENSE file (MIT)
 */

package content

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

/** Shared visual foundation for Simple Rules content pages. */
object ContentStyles:
  val Css =
    """:root{color-scheme:light;--text:#111;--muted:#6b7280;--border:#e5e7eb;--soft:#f7f7f8;--green:#16803c;--amber:#a16207;--red:#dc2626}
      |*{box-sizing:border-box}body{margin:0;background:#fff;color:var(--text);font:15px/1.55 Inter,ui-sans-serif,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;-webkit-font-smoothing:antialiased}.topbar{height:1px;background:var(--border)}.shell{width:min(1040px,calc(100% - 48px));margin:auto}.masthead{display:flex;justify-content:space-between;align-items:center;gap:24px;padding:42px 0 24px;border-bottom:1px solid var(--border)}.brand{margin:2px 0 0;font-size:1.25rem;font-weight:650;letter-spacing:-.02em}.eyebrow,.muted,.checked,.metric-label{color:var(--muted)}.eyebrow{margin:0;font-size:.75rem;font-weight:600;letter-spacing:.06em;text-transform:uppercase}.checked{margin:0;font-size:.85rem}.overview{display:grid;grid-template-columns:2fr 1fr;gap:16px;padding:24px 0}.panel,.table-wrap{border:1px solid var(--border);border-radius:10px;background:#fff}.panel{padding:22px}.main-status{display:flex;align-items:center;justify-content:space-between;gap:20px}.main-status h1{margin:4px 0 0;font-size:1.65rem;line-height:1.2;letter-spacing:-.035em}.status{display:inline-flex;align-items:center;gap:8px;margin:0;font-size:.875rem;font-weight:600}.status:before{content:"";width:8px;height:8px;border-radius:50%;background:currentColor}.state-online{color:var(--green)}.state-degraded{color:var(--amber)}.state-offline{color:var(--red)}.metric{margin:0;font-size:1.75rem;font-weight:650;line-height:1.2;letter-spacing:-.035em}.metric-label{margin:4px 0 0;font-size:.85rem}.section-heading{display:flex;justify-content:space-between;align-items:baseline;gap:16px;margin:18px 0 10px}.section-heading h2{margin:0;font-size:1rem;font-weight:650}.table-wrap{overflow-x:auto}table{width:100%;border-collapse:collapse;text-align:left}caption{padding:13px 16px;text-align:left;color:var(--muted);font-size:.82rem}th,td{padding:13px 16px;border-top:1px solid var(--border);white-space:nowrap}th{background:var(--soft);color:var(--muted);font-size:.72rem;font-weight:600;letter-spacing:.06em;text-transform:uppercase}tbody tr:hover{background:#fafafa}.node-name{font-weight:600}.empty{padding:28px;text-align:center;color:var(--muted)}.resources{padding-top:12px}.link-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.link-card{padding:0;transition:border-color .15s ease,background .15s ease}.link-card:hover{border-color:#c7cbd1;background:#fafafa}.link-card-inner{display:flex;align-items:center;justify-content:space-between;gap:16px;padding:18px}.link-copy{min-width:0}.link-title{color:var(--text);font-weight:600;text-decoration:none}.link-title:hover{text-decoration:underline}.link-description{margin:3px 0 0;color:var(--muted);font-size:.82rem}.link-arrow{color:var(--muted);font-size:1.1rem}.footer{padding:28px 0 44px;color:var(--muted);font-size:.8rem}
      |@media(max-width:680px){.shell{width:calc(100% - 28px)}.masthead{align-items:flex-start;flex-direction:column;padding-top:28px}.overview,.link-grid{grid-template-columns:1fr}.panel{padding:18px}.link-card{padding:0}th,td{padding:11px 12px}}""".stripMargin

  val CspHash: String =
    val digest = MessageDigest.getInstance("SHA-256").digest(Css.getBytes(StandardCharsets.UTF_8))
    "sha256-" + Base64.getEncoder.encodeToString(digest)
