logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.13")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.8")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")