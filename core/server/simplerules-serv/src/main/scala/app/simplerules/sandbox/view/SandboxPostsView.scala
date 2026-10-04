package app.simplerules.sandbox.view

import app.layout.MainLayout
import app.simplerules.sandbox.model.SandboxPostsPageModel
import content.{ContentPost, ContentStyles}
import framework.html.{Html, HtmlDsl}
import framework.html.HtmlDsl.*
import framework.mvc.View

final class SandboxPostsView(model: SandboxPostsPageModel) extends View:
  override def render: Html =
    new MainLayout(model.title, SandboxPostsView.Css).wrap(
      main(cls := "sandbox-posts shell")(
        header(cls := "blog-header")(
          p(cls := "eyebrow")("Simple Rules Journal"),
          h1(model.title),
          p(cls := "intro")("Notes on small systems, Scala, content design, and building software with understandable rules."),
          p(cls := "post-count")(s"${model.content.totalCount} public posts")
        ),
        renderPosts(model.content.posts)
      )
    )

  private def renderPosts(posts: Vector[ContentPost]): Html =
    if posts.isEmpty then p(cls := "empty")("No posts available.")
    else section(cls := "post-list")(posts.map(renderPost))

  private def renderPost(post: ContentPost): Html =
    article(cls := "post panel")(
      div(cls := "post-heading")(
        h2(post.title.data),
        span(cls := "post-type")(post.contentType.toString)
      ),
      post.text.fold(Html.empty)(value => p(cls := "post-text")(value.data)),
      post.url.fold(Html.empty)(value =>
        p(cls := "source")(
          a(href := value.data, target := "_blank", rel := "noopener noreferrer")(
            "Open linked resource"
          )
        )
      ),
      p(cls := "post-meta")(
        s"By ${post.creator.longName.data} · ${post.createdTimestamp} · ${post.group.fold("Public")(_.groupName.data)}"
      )
    )

object SandboxPostsView:
  private val BlogCss =
    ".sandbox-posts{max-width:800px;padding:48px 0 72px}" +
    ".blog-header{padding:0 0 28px;border-bottom:1px solid var(--border)}.blog-header h1{margin:4px 0;font-size:3rem;line-height:1.05;letter-spacing:-.04em}" +
    ".post-count,.post-meta,.source{color:var(--muted);font-size:.82rem}" +
    ".intro{max-width:620px;font-size:1.08rem}.post-list{display:grid;gap:20px;margin-top:28px}" +
    ".post{padding:26px 28px}" +
    ".post-heading{display:flex;justify-content:space-between;align-items:start;gap:18px}.post h2{margin:0 0 10px;font-size:1.45rem;line-height:1.25}" +
    ".post-type{padding:3px 8px;color:var(--green);background:var(--soft);border-radius:999px;font-size:.68rem;font-weight:700;text-transform:uppercase}" +
    ".post-text{white-space:pre-wrap}.source{overflow-wrap:anywhere}.source a{color:var(--text);font-weight:600;text-underline-offset:3px}.source a:hover{color:var(--green)}.post-meta{margin:18px 0 0;padding-top:14px;border-top:1px solid var(--border)}" +
    ".empty{padding:40px;text-align:center;color:var(--muted)}@media(max-width:600px){.blog-header h1{font-size:2.25rem}.post{padding:20px}.post-heading{display:block}.post-type{display:inline-block;margin-bottom:12px}}"

  val Css: String = ContentStyles.Css + BlogCss
