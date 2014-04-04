name := "Team2A"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.0.1",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.mnode.ical4j" % "ical4j" % "1.0.5.2",
  "commons-io" % "commons-io" % "2.3"
)     

play.Project.playJavaSettings
