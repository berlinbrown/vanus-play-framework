# vanus-play-framework

Vanus Play Framework - Web Server, Framework and Browser Client for a New Simpler Web

## Vanus Play Framework

**A toy web server and application framework for a simpler, saner web.**

Vanus Play is an experimental Java web server and application framework built around a simple idea:

> **The web does not need to be complicated.**

The goal of the project is to **support and enforce a simpler, saner web system with rules.**

Vanus Play is intentionally opinionated. It explores what a modern web could look like if we removed layers of complexity that have accumulated around traditional websites and applications.

## The Rules

Vanus Play starts with a few simple rules:

* No JavaScript.
* No `<script>` tags.
* No client-side application frameworks.
* No massive frontend dependency trees.
* HTML is for documents and content.
* CSS is for presentation.
* HTTP is for communication.
* The server handles application logic.
* Forms and links are first-class interactions.
* Pages should work without requiring a client-side runtime.
* Web content should remain readable by both humans and machines.

These rules are not intended to limit what the web can do.

They are intended to explore **what the web could do if we stopped making the browser responsible for everything.**

## Philosophy

Modern web applications often look like this:

```text
Browser
   ↓
HTML
   ↓
JavaScript
   ↓
Frontend Framework
   ↓
API
   ↓
JSON
   ↓
Application Server
   ↓
Database
```

Vanus Play experiments with something simpler:

```text
Browser
   ↓
HTTP
   ↓
Vanus Play
   ↓
Application
   ↓
HTML
```

The server is responsible for the application.

The browser receives a document.

The user interacts with links, forms, and normal web controls.

## Why?

The web started with a remarkably simple model:

```text
request → document → response
```

Over time, many web applications have evolved into distributed client-side software platforms.

Vanus Play asks:

> **What if we kept the simplicity of the original Web while still building modern applications?**

This project is an exploration of that question.

## Built on NanoHTTPD

Vanus Play incorporates and modifies source code originally derived from [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd).

NanoHTTPD provides the underlying HTTP server functionality.

Vanus Play builds its own web and application model on top of that foundation.

The project intentionally includes the NanoHTTPD source rather than treating it as an external dependency so that the server can be experimented with, modified, and evolved as part of the project.

## Development Vanus Play Server

### sbt project compiled with Scala 3

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

### Test coverage

Coverage percentages are produced with sbt-scoverage (Scala sources only):

```bash
cs launch sbt -- coverage test coverageReport
```

The summary prints statement and branch coverage, and reports are written to
`target/scala-3.8.4/scoverage-report/index.html`.


### Development Usage

Visit http://localhost:8080 in your favorite browser or the Vanus Browser

## Licensing

Vanus Play contains both original Vanus Play code and modified source code originally derived from [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd).

### Vanus Play

Original New Web source code is licensed under the **MIT License**.

See [`LICENSE`](LICENSE) for the complete license text.

### NanoHTTPD

Parts of New Web are derived from NanoHTTPD and remain licensed under the **BSD 3-Clause License**.

The original NanoHTTPD copyright and license notices are retained in the applicable source files.

NanoHTTPD:

> Copyright (C) 2012-2016 nanohttpd

See [`src/main/java/org/nanohttpd`](src/main/java/org/nanohttpd) for the NanoHTTPD-derived source code and [`THIRD_PARTY.md`](THIRD_PARTY.md) for additional attribution information.

Modifications to NanoHTTPD-derived source code were made for the New Web project and are identified within the applicable source files.

### License Summary

| Component              | License      |
| ---------------------- | ------------ |
| New Web original code  | MIT          |
| NanoHTTPD-derived code | BSD 3-Clause |


