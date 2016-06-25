name := """team-18-web-project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "org.mongodb" % "mongodb-driver-reactivestreams" % "1.2.0",
  "mysql" % "mysql-connector-java" % "5.1.28",
  "org.avaje.ebeanorm" % "avaje-ebeanorm" % "4.6.2",
  "javax.json" % "javax.json-api" % "1.0",
  "org.json" % "json" % "20160212"
)