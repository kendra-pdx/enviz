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
      Modules.akka("contrib"),
      Modules.spray("routing"),
      Modules.slf4j_api
    )
  )

lazy val uiServer =  project.in(file("ui-server"))
  .settings(common: _*)
  .settings(
    name := "ui-server",
    libraryDependencies ++= Seq(
      Modules.slf4j_api
    )
  )
  .dependsOn(serverCommon)

lazy val sseServer = project.in(file("sse-server"))
  .settings(common: _*)
  .settings(
    name := "sse-server",
    libraryDependencies ++= Seq(
      Modules.akka("contrib"),
      Modules.spray("httpx"),
      Modules.spray_json
    ))
  .dependsOn(serverCommon)

lazy val enviz =  project.in(file("."))
  .settings(common ++ Revolver.settings: _*)
  .settings(
    name := "enviz",
    libraryDependencies ++= Seq(
      Modules.akka("slf4j"),
      Modules.spray("can"),
      Modules.spray("routing")),
    libraryDependencies ++= Modules.logging)
  .aggregate(dropsJS, uiServer, sseServer)
  .dependsOn(uiServer, sseServer)

lazy val Modules = new {
  def spray(name: String) = "io.spray" %% s"spray-$name" % "1.3.2"
  def akka(name: String) = "com.typesafe.akka" %% s"akka-$name" % "2.3.8"
  def slf4j(name: String) = "org.slf4j" % s"slf4j-$name" % "1.7.10"

  lazy val spray_json = "io.spray" %%  "spray-json" % "1.3.1"

  lazy val slf4j_api = slf4j("api")
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"

  lazy val logging =
    slf4j_api :: logback :: Nil
}

lazy val gatherJavaScripts = taskKey[Seq[File]]("get the output of building js")

(gatherJavaScripts in uiServer) := {
  (scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys.fullOptJS in (dropsJS, Compile)).value
  (Seq.empty[File] /: List("*.js", "*.map")) { (files, pattern) ⇒
    files ++ ((crossTarget in dropsJS).value ** pattern).get
  }
}

(resourceGenerators in (uiServer, Compile)) += {
  (gatherJavaScripts in uiServer).taskValue
}