name := "stakeholderApp"

version := "0.1"

val vertxVersion = "3.9.1"
val monixVersion = "3.2.1"
val scalaBase = "2.12.8"

scalaVersion := scalaBase

parallelExecution in ThisBuild := false

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scala-lang" % "scala-reflect" % scalaBase,
  "org.eclipse.californium" % "californium-core" % "2.2.2",
  "org.eclipse.californium" % "element-connector" % "2.2.2",
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "io.monix" %% "monix-reactive" % monixVersion,
  "io.vertx" %% "vertx-lang-scala" % vertxVersion,
  "io.vertx" %% "vertx-web-scala" % vertxVersion,
  "io.vertx" %% "vertx-auth-jwt-scala" % vertxVersion,
  "io.lemonlabs" %% "scala-uri" % "2.2.2",
  "io.vertx" %% "vertx-web-client-scala" % vertxVersion
)