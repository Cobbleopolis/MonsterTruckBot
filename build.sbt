val projectName: String = "MonsterTruckBot"

val displayName: String = "Monster Truck Bot"

val projectVersion: String = "2.2.1"

val discord4JVersion: String = "2.8.2"

val kittehIRCVersion: String = "3.2.0"

lazy val commonDependencies = Seq(
    "com.typesafe.play" %% "anorm" % "2.5.3",
    jdbc,
    guice,
    ws,
    "com.typesafe.play" %% "play-json" % "2.6.2"
)

lazy val commonSettings = Seq(
    name := projectName,
    organization := "com.cobble.bot",
    version := projectVersion,
    scalaVersion := "2.12.2",
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

lazy val `monstertruckbot` = (project in file(".")).enablePlugins(PlayScala, JavaServerAppPackaging, DebianPlugin).settings(commonSettings: _*)
    .settings(
        resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
        unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test").value,
        libraryDependencies ++= Seq(jdbc, ehcache, specs2 % Test, evolutions),
        libraryDependencies ++= Seq(
            "org.postgresql" % "postgresql" % "42.0.0.jre7",
            "org.webjars" %% "webjars-play" % "2.6.1",
            "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
            "org.webjars.npm" % "bootstrap-sass" % "3.3.7",
            "org.webjars.bower" % "font-awesome-sass" % "4.6.2",
            "org.webjars" % "jquery" % "3.2.1",
            "com.github.marcospereira" %% "play-hocon-i18n" % "1.0.1",
            "org.julienrf" %% "play-jsmessages" % "3.0.0"
        ),
        maintainer in Linux := "Cobbleopolis <cobbleopolis@gmail.com>",
        packageSummary in Linux := s"$displayName server",
        packageDescription := s"A server that runs the $displayName website, Discord bot and, Twitch bot",
        debianPackageDependencies in Debian ++= Seq("default-jre | java6-runtime"),
        bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/production.conf"""",
        scalacOptions in Compile in doc ++= Seq(
            "-doc-version", version.value,
            "-doc-title", name.value,
            "-doc-root-content", baseDirectory.value + "/root-doc.txt"
        ),
        routesGenerator := InjectedRoutesGenerator
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
            "org.kitteh.irc" % "client-lib" % kittehIRCVersion
        )
    )
    .dependsOn(`monstertruckbot-common`)
    .aggregate(`monstertruckbot-common`)

lazy val `monstertruckbot-common` = (project in file("modules/common")).enablePlugins(PlayScala, BuildInfoPlugin).settings(commonSettings: _*)
    .settings(
        name += "-common",
        libraryDependencies ++= Seq(cacheApi),
        buildInfoKeys := Seq[BuildInfoKey]("name" -> projectName, "displayName" -> displayName, version, scalaVersion, sbtVersion)
    )