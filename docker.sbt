
enablePlugins(DockerPlugin)

routesImport ++= Seq(
  "ifs.models._",
  "com.wellfactored.playbindings.ValueClassUrlBinders._"
)

maintainer := "Doug Clinton <doug.clinton@digital.bis.gov.uk>"

dockerBaseImage := "openjdk:8u102-jdk"

dockerRepository := Some("rifs")

dockerExposedPorts := Seq(9000)

dockerUpdateLatest := true