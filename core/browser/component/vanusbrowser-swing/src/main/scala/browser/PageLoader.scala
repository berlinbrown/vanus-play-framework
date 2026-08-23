package browser

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.*
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.*

/** Fetches a page and extracts its visible text content. */
object PageLoader:
  private val browser = JsoupBrowser()

  def loadPageText(url: String): String =
    val doc = browser.get(url)
    doc >> text("body")
