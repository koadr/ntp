name := "ntp"

version := "1.0"

scalaVersion := "2.10.3"

mainClass in (Compile,run) := Some("com.koadr.Main")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.3",
  "ch.qos.logback" % "logback-classic" % "1.0.9" % "runtime",
  "org.joda" % "joda-convert" % "1.6",
  "joda-time" % "joda-time" % "2.3",
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.3" % "test"
)
    