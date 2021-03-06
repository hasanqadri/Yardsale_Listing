name := """yardsale-finder"""

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
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "mysql" % "mysql-connector-java" % "5.1.28",
  "org.avaje.ebeanorm" % "avaje-ebeanorm" % "4.6.2",
  "javax.json" % "javax.json-api" % "1.0",
  "org.json" % "json" % "20160212",
  "com.google.zxing" % "core" % "2.0",
  "com.google.zxing" % "javase" % "3.2.1",
  "org.gnu" % "gnu-crypto" % "2.0.1",
  "com.typesafe.play" %% "play-mailer" % "5.0.0"

)



fork in run := true
