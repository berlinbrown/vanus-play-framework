# vanusplay default server

## sbt project compiled with Scala 3 (targets Java 21 bytecode)

### Development - Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

### Development - Package and Run

Build a self-contained executable JAR:

```bash
cs launch sbt -- assembly
```

The JAR is written to `target/scala-3.8.4/vanusplay-assembly.jar` and includes
the files under `src/main/resources`.

To build and run it locally:

```bash
./scripts/launch-local.sh
```

Arguments are passed to `WebServerMain`, for example:

```bash
./scripts/launch-local.sh -p 8080 -d .
```

Useful flags:

- `-h` / `--host` — bind address (defaults to `127.0.0.1`, localhost only; pass a LAN address to share)
- `-q` / `--quiet` — suppress request logging
- `--cors` / `--cors=origin` — enable CORS headers
- `--dir-listing` — list directory contents when no index file exists (off by default)
- `--rate-limit <n>` — allow at most `n` requests per client IP per 10-second window (off by default); excess requests get `429 Too Many Requests`

### Test coverage

Coverage percentages are produced with sbt-scoverage (Scala sources only):

```bash
cs launch sbt -- coverage test coverageReport
```

The summary prints statement and branch coverage, and reports are written to
`target/scala-3.8.4/scoverage-report/index.html`.

### Restricted static serving

Vanus uses ordinary Scala and Java 21 APIs. No preview features, additional runtime
libraries, or compiler extensions are required. The Java 21 bytecode target is unchanged.

Only these file extensions are served (case insensitive):

- HTML/HTM, CSS, TXT
- PNG, JPG/JPEG, GIF, ICO, WebP

JavaScript (including JS/MJS), SVG, JSON, XML, and all other extensions are rejected.
The allowlist and MIME mappings live in `VanusSecurity.scala`. Directory listings hide
unlisted file types. Static files support GET and HEAD; OPTIONS describes allowed methods.
Application routes can explicitly register other methods before server construction.

Every response from the Vanus server, including parser errors, redirects, partial
responses, and plugin responses, receives `X-Content-Type-Options: nosniff` and this CSP:

```text
default-src 'self'; script-src 'none'; style-src 'self' 'unsafe-inline'; object-src 'none'; frame-src 'none'; worker-src 'none'; base-uri 'none'; form-action 'none'; frame-ancestors 'none'; sandbox allow-same-origin
```

HTML/HTM is served as HTML, with JavaScript blocked by the browser's CSP enforcement.
Inline scripts, external scripts, event handlers, workers, and embedded frames are
not permitted. Local CSS and inline styles remain available. Forms are disabled.
This does not remove script text from downloaded files; the policy applies when a
browser loads a response from this server. Generated HTML also escapes filenames
and paths. Document roots should be writable only by trusted local users; canonical
path checks do not provide isolation against a hostile process changing directories
while a request is opening a file.

### Connection and resource limits

Accepted connections run on Java 21 virtual threads. A separate connection cap bounds
sockets; a request cap bounds concurrent handler execution. Excess connections close
immediately, while excess admitted requests receive 503. The existing per-IP request
limit returns 429 and retains at most 10,000 client windows, removing expired entries
as traffic arrives. Client addresses come from the socket, not forwarded headers.

| Option | Default | Meaning |
| --- | --- | --- |
| `--max-connections` | 256 | Maximum admitted connections |
| `--max-in-flight` | 32 | Concurrent handler calls; file transmissions remain bounded by connections |
| `--max-header-bytes` | 8192 | Header block limit; maximum configurable value is 65536 |
| `--max-body-bytes` | 1048576 | Body limit; maximum configurable value is 16 MiB |
| `--max-temp-bytes` | 67108864 | Shared upload storage reservation budget |
| `--max-multipart-parts` | 32 | Maximum multipart parts per body |
| `--max-keep-alive-requests` | 100 | Maximum requests on one connection |
| `--idle-timeout-ms` | 5000 | Waiting for another request |
| `--header-timeout-ms` | 10000 | Total time to complete headers after the first read |
| `--body-timeout-ms` | 15000 | Total time for a handler to consume its body |
| `--request-timeout-ms` | 30000 | Handler deadline, including body consumption |
| `--write-timeout-ms` | 30000 | Total response transmission deadline |
| `--shutdown-ms` | 10000 | Grace period for active work during shutdown |

For example:

```bash
./scripts/launch-local.sh -d ./public --max-connections 128 --max-in-flight 16 --rate-limit 100
```

The deadline monitor checks connections every 50 ms. Deadlines close the socket and
interrupt its worker. Handlers must honor interruption; Java does not safely force-stop
arbitrary application code. Large downloads must fit the configured transmission deadline.

At most four bodies are parsed concurrently. Before creating upload files, a request
reserves twice its configured maximum body size (the stored body plus extracted parts).
The reservation is released after successful cleanup. A failed deletion retains its
reservation and emits a warning. Custom handlers and plugins must use the supplied
body and temporary-file APIs to stay within these budgets.

Unread request bodies cause connection closure rather than reuse. Duplicate framing
headers, invalid content lengths, and oversized requests are rejected. Request
Transfer-Encoding and Expect are deliberately unsupported and receive 501 and 417,
respectively; combining Transfer-Encoding with Content-Length receives 400. Chunked
responses remain supported. Single byte ranges are supported; malformed or multiple
ranges are ignored and served as full responses. Metadata-based ETags are weak validators,
so If-Range only permits a range when its date matches the file's last-modified time.

Routes, plugin mappings, and index filenames are immutable snapshots per server.
Register them before creating the server; later registrations do not alter a running
instance. Plugin instances themselves must support concurrent use. Internal rewrites
are limited to eight hops and stay subject to the file and response-type policies.

### Lifecycle and checks

The launcher runs as a service until interrupted or terminated; it no longer waits for
Enter. On JVM shutdown it stops accepting connections, closes idle clients, allows
active requests to finish within the grace period, then closes remaining sockets and
interrupts workers. Invalid arguments or unreadable document roots fail before binding.

JDK logging records request method, status, and handler duration without dumping headers,
query strings, or credentials. `--quiet` suppresses access logs. Server counters expose
active/rejected connections, active/rejected/total requests, and reserved temporary bytes.
The duration measures handler execution, not the subsequent response transmission.

Run compilation, unit/integration tests, and executable-JAR packaging with:

```bash
sbt clean test assembly
```

`ServerHardeningSuite` exercises real sockets for CSP and file restrictions, ranges,
persistent connections, framing rejection, virtual threads, overload recovery, deadlines,
and graceful shutdown. These are regression checks, not a throughput capacity guarantee.
