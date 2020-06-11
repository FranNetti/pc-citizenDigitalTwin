name := "cop-medic-app"

version := "0.1"

scalaVersion := "2.12.8"

val vertxVersion = "3.9.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.eclipse.californium" % "californium-core" % "2.2.2",
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "io.vertx" %% "vertx-lang-scala" % vertxVersion,
  "io.vertx" %% "vertx-web-scala" % vertxVersion,
  "io.vertx" %% "vertx-auth-jwt-scala" % vertxVersion,
  "io.lemonlabs" %% "scala-uri" % "2.2.2",
  "io.vertx" %% "vertx-web-client-scala" % vertxVersion

)