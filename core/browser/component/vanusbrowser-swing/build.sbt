val scala3Version = "3.8.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "vanusbrowser-swing",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.3.4" % Test,
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "3.1.1",

    // run the Swing app in a forked JVM so `sbt run` returns control of the shell
    fork := true
  )
