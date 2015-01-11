lazy val common: Seq[Def.Setting[_]] = Seq(
  scalaVersion := "2.11.5",
  organization := "me.enkode",
  version := "0-SNAPSHOT",
  resolvers += "spray repo" at "http://repo.spray.io"
)

lazy val dropsJS = project.in(file("drops-js"))
  .settings(common: _*)
  .settings(scalaJSSettings: _*)
  .settings(name := "drops-js")

lazy val serverCommon = project.in(file("server-common"))
  .settings(common: _*)
  .settings(
    name := "server-common",
    libraryDependencies ++= Seq(
      Modules.akka("actor"),
      Modules.spray("routing")
    )
  )

lazy val uiServer =  project.in(file("ui-server"))
  .settings(common: _*)
  .settings(name := "ui-server")
  .dependsOn(serverCommon)

lazy val sseServer = project.in(file("sse-server"))
  .settings(common: _*)
  .settings(name := "sse-server")
  .dependsOn(serverCommon)

lazy val enviz =  project.in(file("."))
  .settings(common ++ Revolver.settings: _*)
  .settings(
    name := "enviz",
    libraryDependencies ++= Seq(
      Modules.spray("can"),
      Modules.spray("routing")
    ))
  .aggregate(dropsJS, uiServer, sseServer)
  .dependsOn(uiServer, sseServer)

lazy val Modules = new {
  def spray(name: String) = "io.spray" %% s"spray-$name" % "1.3.2"
  def akka(name: String) = "com.typesafe.akka" %% s"akka-$name" % "2.3.8"
}

lazy val gatherJavaScripts = taskKey[Seq[File]]("get the output of building js")

(gatherJavaScripts in uiServer) := {
  (scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys.fullOptJS in (dropsJS, Compile)).value
  ((crossTarget in dropsJS).value ** "*.js").get
}

(resourceGenerators in (uiServer, Compile)) += {
  (gatherJavaScripts in uiServer).taskValue
}