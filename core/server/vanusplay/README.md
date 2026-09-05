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
