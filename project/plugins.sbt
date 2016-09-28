
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M5")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.6.0")


// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.0")
