name := "hack4good"

version := "0.0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.8.4",
  "net.databinder" %% "unfiltered-directives" % "0.8.4",
  "net.databinder" %% "unfiltered-filter" % "0.8.4",
  "net.databinder" %% "unfiltered-jetty" % "0.8.4",
  "net.databinder" %% "unfiltered-util" % "0.8.4",
  "com.quantifind" %% "sumac" % "0.3.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.twitter" %% "util-collection" % "6.27.0",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "joda-time" % "joda-time" % "2.9.1",			
  "com.h2database" % "h2" % "1.3.166",
  "org.json4s" %% "json4s-native" % "3.3.0"
)


ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"
