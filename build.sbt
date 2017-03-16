val projectName: String = "MonsterTruckBot"

val projectVersion: String = "1.0.0-SNAPSHOT"

val discord4JVersion: String = "2.7.0"

val pIRCBotXVersion: String = "2.1"

lazy val commonDependencies = Seq(
    "com.typesafe.play" %% "anorm" % "2.5.3"
)

lazy val commonSettings = Seq(
    name := projectName,
    organization := "com.cobble.bot",
    version := projectVersion,
    scalaVersion := "2.11.7",
    isSnapshot := version.value.toLowerCase.contains("snapshot"),
    target in Compile in doc := baseDirectory.value / "docs",
    crossPaths := false,
    autoAPIMappings := true,
    apiMappings += (scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")),
    libraryDependencies ++= commonDependencies,
    buildInfoKeys := Seq[BuildInfoKey]("name" -> projectName, version, scalaVersion, sbtVersion),
    resolvers ++= Seq(
        Resolver.jcenterRepo,
        "jitpack" at "https://jitpack.io"
    )
)

lazy val `monstertruckbot` = (project in file(".")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
        unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test").value,
        libraryDependencies ++= Seq(jdbc, cache, ws, specs2 % Test),
        libraryDependencies ++= Seq(
            "org.postgresql" % "postgresql" % "42.0.0.jre7"
        )
    )
    .dependsOn(discord, twitch, common)
    .aggregate(discord, twitch, common)

lazy val discord = (project in file("modules/discord")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        name += "-discord",
        libraryDependencies ++= Seq(
            "com.github.austinv11" % "Discord4j" % discord4JVersion
        )
    )
    .dependsOn(common)
    .aggregate(common)

lazy val twitch = (project in file("modules/twitch")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        name += "-twitch",
        libraryDependencies ++= Seq(
            "org.pircbotx" % "pircbotx" % pIRCBotXVersion
        )
    )
    .dependsOn(common)
    .aggregate(common)

lazy val common = (project in file("modules/common")).enablePlugins(PlayScala, BuildInfoPlugin).settings(commonSettings: _*)
    .settings(
        name += "-common",
        libraryDependencies ++= Seq(jdbc)
    )