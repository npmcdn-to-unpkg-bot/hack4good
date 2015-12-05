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
  "com.twitter" %% "util-collection" % "6.27.0"

)


ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"
