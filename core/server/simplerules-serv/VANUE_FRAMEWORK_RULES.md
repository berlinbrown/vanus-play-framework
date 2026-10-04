# Framework

JavaScript (including JS/MJS), SVG, JSON, XML, and all other extensions are rejected.
The allowlist and MIME mappings live in `VanusSecurity.scala`. Directory listings hide
unlisted file types. Static files support GET and HEAD; OPTIONS describes allowed methods.
Application routes can explicitly register other methods before server construction.

Every response from the Vanus server, including parser errors, redirects, partial
responses, and plugin responses, receives `X-Content-Type-Options: nosniff` and this CSP:

```text
default-src 'none'; script-src 'self'; style-src 'self' 'sha256-<messages-style-hash>'; img-src 'self'; connect-src 'self'; font-src 'self'; object-src 'none'; media-src 'none'; frame-src 'none'; worker-src 'none'; base-uri 'none'; form-action 'self'; frame-ancestors 'none'; upgrade-insecure-requests;
```

HTML/HTM is served as HTML. Scripts, styles, images, connections, fonts, and form
submissions are restricted to the same origin. Inline scripts and arbitrary inline styles
are blocked; the embedded messages stylesheet is allowed only by its exact SHA-256 hash.
Objects, media, workers, and embedded frames are not permitted. HTTP subresources are upgraded.
This does not remove script text from downloaded files; the policy applies when a
browser loads a response from this server. Generated HTML also escapes filenames
and paths. Document roots should be writable only by trusted local users; canonical
path checks do not provide isolation against a hostile process changing directories
while a request is opening a file.


# Rules

* xhtml validation - for input HTML, throws an error if not valid   