name := """play-scala-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.4.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick" % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0"
)
libraryDependencies += "com.mysql" % "mysql-connector-j" % "8.3.0"
libraryDependencies += "com.h2database" % "h2" % "2.2.224"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
