name := """team-18-web-project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  jdbc,
  "mysql" % "mysql-connector-java" % "5.1.36",
  cache,
  javaWs,
  evolutions,
  "org.mongodb" % "mongodb-driver-reactivestreams" % "1.2.0",
  "mysql" % "mysql-connector-java" % "5.1.28",
  "org.avaje.ebeanorm" % "avaje-ebeanorm" % "4.6.2"
)