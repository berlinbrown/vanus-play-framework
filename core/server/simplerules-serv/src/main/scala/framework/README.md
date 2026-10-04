# Scala MVC and HTML DSL

`framework.html` owns a small immutable HTML tree and one renderer. Text nodes escape markup delimiters; attribute values additionally escape quotes. Attribute and tag names are validated, event-handler attributes are rejected, URL-bearing attributes reject non-allowlisted schemes, and void elements cannot contain children. Raw HTML is intentionally unsupported so callers cannot accidentally bypass escaping.

`framework.mvc` separates request context, controllers, views, layouts, and response results. `framework.mvc.Router` maps NanoHTTPD methods and paths to controllers, while the small `MvcNanoletHandler` bridge adapts controller results to NanoHTTPD responses. The web server, rather than the MVC layer, remains responsible for applying its shared security headers.

Application views and styles live under `app`; CSS is embedded from Scala constants in the generated document. This keeps the initial MVC layer dependency-free and avoids introducing a template language or general-purpose rendering framework.