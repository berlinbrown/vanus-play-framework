val scala3Version = "3.8.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "vanusplay",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    // Target Java 21 bytecode so the JAR runs on a Java 21 server
    Compile / scalacOptions += "-release:21",
    Compile / javacOptions ++= Seq("--release", "21"),

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.3.4" % Test,
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
      "junit" % "junit" % "4.13.2" % Test,
      "org.apache.httpcomponents" % "httpclient" % "4.3.6" % Test,
      "org.apache.httpcomponents" % "httpmime" % "4.3.6" % Test
    ),

    Test / parallelExecution := false,

    Compile / run / mainClass := Some("WebServerMain"),
    assembly / mainClass := Some("WebServerMain"),
    assembly / assemblyJarName := "vanusplay-assembly.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", _*) => MergeStrategy.concat
      case PathList("META-INF", "nanohttpd", _*) => MergeStrategy.first
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )
