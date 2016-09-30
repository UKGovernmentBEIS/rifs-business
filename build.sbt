import de.heikoseeberger.sbtheader.CommentStyleMapping._
import de.heikoseeberger.sbtheader.license.MIT


name := "rifs-business"

headers in ThisBuild := createFrom(MIT, "2016", "Department of Business, Energy and Industrial Strategy")
git.useGitDescribe in ThisBuild := true

scalaVersion in ThisBuild := "2.11.8"
lazy val models = (project in file("models"))
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)

lazy val slickGen = (project in file("slickGen"))
  .dependsOn(models).aggregate(models)
  .settings(libraryDependencies ++= Seq(
    "com.wellfactored" %% "property-info" % "1.0.0",
    "com.typesafe.slick" %% "slick" % "3.1.1"
  ))
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)

lazy val slicks = (project in file("slicks"))
  .dependsOn(models).aggregate(models)
  .dependsOn(slickGen).aggregate(slickGen)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0"
    )
  )

lazy val `rifs-business` = (project in file("."))
  .dependsOn(slicks).aggregate(slicks)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)
  .settings(
    libraryDependencies ++= Seq(
      cache,
      "com.wellfactored" %% "play-bindings" % "1.1.0",
      "com.github.melrief" %% "pureconfig" % "0.1.6",
      "org.postgresql" % "postgresql" % "9.4.1211",
      "mysql" % "mysql-connector-java" % "6.0.3",
      "com.typesafe.play" %% "play-slick" % "2.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
    ),
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9100"),
    routesImport ++= Seq(
      "rifs.models._",
      "com.wellfactored.playbindings.ValueClassUrlBinders._"
    ),
    javaOptions := Seq(
      "-Dconfig.file=src/main/resources/development.application.conf",
      "-Dlogger.file=src/main/resources/development.logger.xml"
    )
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
