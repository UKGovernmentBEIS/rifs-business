import de.heikoseeberger.sbtheader.CommentStyleMapping._
import de.heikoseeberger.sbtheader.license.MIT
import sbtbuildinfo.BuildInfoPlugin.autoImport._


name := "rifs-business"

headers in ThisBuild := createFrom(MIT, "2016", "Department of Business, Energy and Industrial Strategy")
git.useGitDescribe in ThisBuild := true

scalaVersion in ThisBuild := "2.11.8"

lazy val slickGen = (project in file("slickGen"))
  .settings(libraryDependencies ++= Seq(
    "com.wellfactored" %% "property-info" % "1.0.0",
    "com.typesafe.slick" %% "slick" % "3.1.1"
  ))
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)

val SLICK_PG_VERSION = "0.14.3"

val slickpgDependencies = Seq(
  "com.github.tminglei" %% "slick-pg" % SLICK_PG_VERSION,
  "com.github.tminglei" %% "slick-pg_play-json" % SLICK_PG_VERSION,
  "com.github.tminglei" %% "slick-pg_date2" % SLICK_PG_VERSION,
  "com.github.tminglei" %% "slick-pg_joda-time" % SLICK_PG_VERSION
)

lazy val `rifs-business` = (project in file("."))
  .dependsOn(slickGen).aggregate(slickGen)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    libraryDependencies ++= Seq(
      cache,
      "com.wellfactored" %% "play-bindings" % "1.1.0",
      "com.github.melrief" %% "pureconfig" % "0.1.6",
      "org.postgresql" % "postgresql" % "9.4.1211",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
      "com.typesafe.play" %% "play-slick" % "2.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
      "com.typesafe.play" %% "play-mailer" % "5.0.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"

    ),
    libraryDependencies ++= slickpgDependencies,
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9100"),
    routesImport ++= Seq(
      "rifs.business.models._",
      "com.wellfactored.playbindings.ValueClassUrlBinders._"
    ),
    javaOptions := Seq(
      "-Dconfig.file=src/main/resources/development.application.conf",
      "-Dlogger.file=src/main/resources/development.logger.xml"
    ),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "rifs.business.buildinfo",
    buildInfoOptions += BuildInfoOption.ToJson
  )
  .enablePlugins(AutomateHeaderPlugin)

fork in Test in ThisBuild := true
testForkedParallel in ThisBuild := true

libraryDependencies in ThisBuild ++= Seq(
  "joda-time" % "joda-time" % "2.9.4",
  "org.joda" % "joda-convert" % "1.7",
  "com.wellfactored" %% "value-wrapper" % "1.1.0",
  "org.scalatest" %% "scalatest" % "2.2.0" % Test
)
