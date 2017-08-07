package twitch

import javax.inject.{Inject, Singleton}

import twitch.api.TwitchCommand
import twitch.commands.{TwitchPingCommand, TwitchSubCountCommand, TwitchVersionCommand}

@Singleton
class TwitchCommandRegistry @Inject()(
                                        val pingCommand: TwitchPingCommand,
                                        val versionCommand: TwitchVersionCommand,
                                        val subCountCommand: TwitchSubCountCommand
                                     ) {

    val commands: Map[String, TwitchCommand] = Map(
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        subCountCommand.name -> subCountCommand
    )

}
