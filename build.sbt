name := "Team2A"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.0",   
  "org.webjars" % "bootstrap" % "3.0.1",
  "org.webjars" % "bootstrap-glyphicons" % "bdd2cbfba0",
  "org.webjars" % "momentjs" % "2.6.0",
  "org.webjars" % "jquery-ui" % "1.10.4",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mnode.ical4j" % "ical4j" % "1.0.5.2",
  "commons-io" % "commons-io" % "2.3",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.apache.httpcomponents" % "httpclient" % "4.0-alpha4",
  "org.mindrot" % "jbcrypt" % "0.3m"
)     

play.Project.playJavaSettings
