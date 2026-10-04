import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

object VanusHomeMessagesStyles:
  val Css =
    """:root{color-scheme:light;--bg:#fff;--panel:#f8fafc;--line:#dbe3ee;--text:#172033;--muted:#66758c;--cyan:#087f71;--violet:#6657c7}
      |*{box-sizing:border-box}body{margin:0;min-height:100vh;background:linear-gradient(180deg,#f6f9fc 0,#fff 260px);color:var(--text);font:14px/1.5 ui-sans-serif,system-ui,-apple-system,sans-serif}
      |body:before{content:"";display:block;height:3px;background:linear-gradient(90deg,var(--cyan),var(--violet))}.hero,.stream,.error{width:min(880px,calc(100% - 32px));margin-inline:auto}.hero{display:flex;justify-content:space-between;align-items:end;gap:24px;padding:64px 0 28px;border-bottom:1px solid var(--line)}
      |h1{margin:.15em 0;font-size:clamp(1.8rem,4vw,3rem);letter-spacing:-.04em;line-height:1}.hero p,.error p{color:var(--muted);margin:0}.eyebrow{color:var(--cyan);font-size:.68rem;font-weight:800;letter-spacing:.2em}.count{white-space:nowrap;color:var(--muted);font-size:.82rem;border:1px solid var(--line);border-radius:999px;padding:6px 11px;background:#fff}
      |.stream{display:grid;gap:12px;padding:28px 0 64px}.message{max-width:82%;padding:14px 16px;background:var(--panel);border:1px solid var(--line);border-radius:5px 18px 18px 18px;box-shadow:0 8px 24px #27364d12}.message.response{justify-self:end;background:#f7f5ff;border-radius:18px 5px 18px 18px;border-color:#dcd7f7}.message header{display:flex;justify-content:space-between;gap:18px}.role{font-weight:750;color:var(--cyan)}.response .role{color:var(--violet)}.meta{color:var(--muted);font-size:.74rem}.message p{margin:7px 0 0;white-space:pre-wrap;overflow-wrap:anywhere}.error{margin-top:18vh;padding:32px;border:1px solid var(--line);border-radius:20px;background:var(--panel);box-shadow:0 12px 36px #27364d14}
      |@media(max-width:600px){.hero{align-items:start;flex-direction:column;padding-top:42px}.message{max-width:94%}.message header{flex-direction:column;gap:2px}}""".stripMargin

  val CspHash: String =
    val digest = MessageDigest.getInstance("SHA-256").digest(Css.getBytes(StandardCharsets.UTF_8))
    "sha256-" + Base64.getEncoder.encodeToString(digest)
