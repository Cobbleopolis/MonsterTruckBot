val projectName: String = "MonsterTruckBot"

val displayName: String = "Monster Truck Bot"

val projectVersion: String = "0.0.1"

val discord4JVersion: String = "2.8.2"

lazy val commonDependencies = Seq(
    "com.typesafe.play" %% "anorm" % "2.5.3"
)

lazy val commonSettings = Seq(
    name := projectName,
    organization := "com.cobble.bot",
    version := projectVersion,
    scalaVersion := "2.11.7",
    isSnapshot := version.value.toLowerCase.contains("snapshot"),
    crossPaths := false,
    autoAPIMappings := true,
    apiMappings += (scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")),
    libraryDependencies ++= commonDependencies,
    resolvers ++= Seq(
        Resolver.jcenterRepo,
        "jitpack" at "https://jitpack.io"
    )
)

lazy val `monstertruckbot` = (project in file(".")).enablePlugins(PlayScala, JavaServerAppPackaging, DebianPlugin, ScalaUnidocPlugin).settings(commonSettings: _*)
    .settings(
        resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
        unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test").value,
        libraryDependencies ++= Seq(jdbc, cache, ws, specs2 % Test, evolutions),
        libraryDependencies ++= Seq(
            "org.postgresql" % "postgresql" % "42.0.0.jre7",
            "org.webjars" %% "webjars-play" % "2.5.0",
            "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3" exclude("org.webjars", "bootstrap") exclude("org.webjars", "jquery"),
            "org.webjars.npm" % "bootstrap-sass" % "3.3.7",
            "org.webjars.bower" % "font-awesome-sass" % "4.6.2",
            "org.webjars" % "jquery" % "3.2.1",
            "ws.securesocial" %% "securesocial" % "3.0-M7",
            "com.github.marcospereira" %% "play-hocon-i18n" % "0.0.2",
            "org.julienrf" %% "play-jsmessages" % "2.0.0"
        ),
        maintainer in Linux := "Cobbleopolis <cobbleopolis@gmail.com>",
        packageSummary in Linux := s"$displayName server",
        packageDescription := s"A server that runs the $displayName website, Discord bot and, Twitch bot",
        debianPackageDependencies in Debian ++= Seq("default-jre | java6-runtime"),
        doc in Compile := (doc in ScalaUnidoc).value,
        target in unidoc in ScalaUnidoc := baseDirectory.value / "docs",
        scalacOptions in Compile in doc ++= Seq(
            "-doc-version", version.value,
            "-doc-title", name.value,
            "-doc-root-content", baseDirectory.value + "/root-doc.txt"
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
        libraryDependencies ++= Seq(jdbc, cache),
        buildInfoKeys := Seq[BuildInfoKey]("name" -> projectName, "displayName" -> displayName, version, scalaVersion, sbtVersion)
    )