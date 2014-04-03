name := "Team2A"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.0.1",
  "org.mnode.ical4j" %% "ical4j" % "1.0.5.2"
)     

play.Project.playJavaSettings
