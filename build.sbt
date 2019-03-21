import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

val projectName: String = "MonsterTruckBot"

val displayName: String = "Monster Truck Bot"

val dockerImageName: String = displayName.toLowerCase.split(" ").mkString("-")

val projectVersion: String = "2.8.0-SNAPSHOT"

val discord4JVersion: String = "2.10.1"

val kittehIRCVersion: String = "4.0.2"

lazy val commonDependencies = Seq(
    "com.typesafe.play" %% "anorm" % "2.5.3",
    jdbc,
    guice,
    ws,
    "com.typesafe.play" %% "play-json" % "2.6.6"
)

val dockerSettings = Seq(
    maintainer in Docker := "Cobbleopolis <cobbleopolis@gmail.com>",
    daemonUser in Docker := packageName.value,
    dockerExposedPorts := Seq(9000),
    dockerExposedVolumes := Seq(s"${(defaultLinuxInstallLocation in Docker).value}/conf"),
    dockerCommands in Docker := dockerCommands.value.take(3) ++ Seq(
        ExecCmd("RUN", "useradd", "-s", "/bin/bash", (daemonUser in Docker).value),
        Cmd("RUN", "apt-get", "-qq", "update", "&&", "apt-get", "-qq", "install", "-y","--no-install-recommends", "curl", ">", "/dev/null", "2>&1", "&&", "rm", "-rf", "/var/lib/apt/lists/*"),
        Cmd("HEALTHCHECK", "--interval=5m", "--start-period=1m", "CMD curl --fail 'http://localhost:9000/discord/alive' || exit 1")
//        ExecCmd("RUN", "addgroup", "-S", (daemonGroup in Docker).value),
//        ExecCmd("RUN", "adduser", "-D", "-H", "-S","-s", "/bin/bash", "-G", (daemonGroup in Docker).value, (daemonUser in Docker).value)
    ) ++ (dockerCommands in Docker).value.drop(3),
    dockerUsername := Some("cobbleopolis"),
    packageName in Docker := dockerImageName,
    dockerUpdateLatest in Docker := !isSnapshot.value,
    dockerBaseImage := "openjdk:jre-slim"
)

lazy val commonSettings = Seq(
    organization := "com.cobble.bot",
    version := projectVersion,
    scalaVersion := "2.12.3",
    isSnapshot := version.value.toLowerCase.contains("snapshot"),
    crossPaths := false,
    autoAPIMappings := true,
    apiMappings += (scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")),
    libraryDependencies ++= commonDependencies,
    resolvers ++= Seq(
        Resolver.jcenterRepo,
        "jitpack" at "https://jitpack.io"
    ),
    JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnTransitiveEvictions(false).withWarnDirectEvictions(true).withWarnScalaVersionEviction(false),
    maintainer in Linux := "Cobbleopolis <cobbleopolis@gmail.com>",
    packageSummary in Linux := s"$displayName server",
    packageDescription := s"A server that runs the $displayName website, Discord bot and, Twitch bot",
    debianPackageDependencies in Debian ++= Seq("default-jre | java8-runtime"),
    javaOptions in Universal ++= Seq(
        "-Dplay.evolutions.db.default.autoApply=true",
        "-Dplay.http.session.secure=true",
        "-Dconfig.resource=production.conf"
    ),
    javaOptions in Debian ++= Seq(
        s"-Dpidfile.path=/var/run/${packageName.value}/RUNNING_PID",
        s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf",
        s"-Dlogger.file=/usr/share/${packageName.value}/conf/production-logback.xml"
    ),
    daemonUser := packageName.value,
    daemonGroup := packageName.value
) ++ dockerSettings

lazy val monstertruckbot = Project(id = "monstertruckbot", base = file(".")).enablePlugins(PlayScala, JavaServerAppPackaging, DebianPlugin, SystemdPlugin, DockerPlugin).settings(commonSettings: _*)
    .settings(
        name := projectName,
        resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
        unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test").value,
        libraryDependencies ++= Seq(jdbc, ehcache, specs2 % Test, evolutions),
        libraryDependencies ++= Seq(
            "org.postgresql" % "postgresql" % "42.0.0.jre7",
            "org.webjars" %% "webjars-play" % "2.6.1",
            "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B4",
            "org.webjars.npm" % "bootstrap" % "4.0.0-beta",
            "org.webjars.npm" % "font-awesome-sass" % "4.7.0",
            "org.webjars" % "jquery" % "3.2.1",
            "org.webjars.npm" % "popper.js" % "1.12.5",
            "com.github.marcospereira" %% "play-hocon-i18n" % "1.0.1",
            "org.julienrf" %% "play-jsmessages" % "3.0.0"
        ),
        scalacOptions in Compile in doc ++= Seq(
            "-doc-version", version.value,
            "-doc-title", name.value,
            "-doc-root-content", baseDirectory.value + "/root-doc.txt"
        ),
        routesGenerator := InjectedRoutesGenerator
    )
    .dependsOn(discord, twitch, common)

lazy val discord = Project(id = "discord", base = file("modules/discord")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        libraryDependencies ++= Seq(
            "com.github.austinv11" % "Discord4j" % discord4JVersion
        )
    )
    .dependsOn(common)
    .aggregate(common)

lazy val twitch = Project(id = "twitch", base = file("modules/twitch")).enablePlugins(PlayScala).settings(commonSettings: _*)
    .settings(
        libraryDependencies ++= Seq(
            "org.kitteh.irc" % "client-lib" % kittehIRCVersion
        )
    )
    .dependsOn(common)
    .aggregate(common)

lazy val common = Project(id = "common", base = file("modules/common")).enablePlugins(PlayScala, BuildInfoPlugin).settings(commonSettings: _*)
    .settings(
        libraryDependencies ++= Seq(cacheApi),
        buildInfoKeys := Seq[BuildInfoKey]("name" -> projectName, "displayName" -> displayName, version, scalaVersion, sbtVersion)
    )