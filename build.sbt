val projectName: String = "MonsterTruckBot"

val displayName: String = "Monster Truck Bot"

val projectVersion: String = "1.0.0-SNAPSHOT"

val discord4JVersion: String = "2.7.0"

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
            "org.postgresql" % "postgresql" % "42.0.0.jre7",
            "org.webjars" %% "webjars-play" % "2.5.0",
            "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3" exclude("org.webjars", "bootstrap"),
            "org.webjars.npm" % "bootstrap-sass" % "3.3.7",
            "org.webjars.bower" % "font-awesome-sass" % "4.6.2",
            "ws.securesocial" %% "securesocial" % "3.0-M7"
        )
    )
    .dependsOn(`monstertruckbot-discord`, `monstertruckbot-twitch`, `monstertruckbot-common`)
    .aggregate(`monstertruckbot-discord`, `monstertruckbot-twitch`, `monstertruckbot-common`)

lazy val `monstertruckbot-discord` = (project in file("modules/discord")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        name += "-discord",
        libraryDependencies ++= Seq(
            "com.github.austinv11" % "Discord4j" % discord4JVersion
        )
    )
    .dependsOn(`monstertruckbot-common`)
    .aggregate(`monstertruckbot-common`)

lazy val `monstertruckbot-twitch` = (project in file("modules/twitch")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        name += "-twitch",
        libraryDependencies ++= Seq(

        )
    )
    .dependsOn(`monstertruckbot-common`)
    .aggregate(`monstertruckbot-common`)

lazy val `monstertruckbot-common` = (project in file("modules/common")).enablePlugins(PlayScala, BuildInfoPlugin).settings(commonSettings: _*)
    .settings(
        name += "-common",
        libraryDependencies ++= Seq(jdbc),
        buildInfoKeys := Seq[BuildInfoKey]("name" -> projectName, "displayName" -> displayName, version, scalaVersion, sbtVersion)
    )