package twitch

import javax.inject.{Inject, Singleton}

import twitch.api.TwitchCommand
import twitch.commands.{TwitchPingCommand, TwitchVersionCommand}

@Singleton
class TwitchCommandRegistry @Inject()(
                                        val pingCommand: TwitchPingCommand,
                                        val versionCommand: TwitchVersionCommand
                                     ) {

    val commands: Map[String, TwitchCommand] = Map(
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand
    )

}
