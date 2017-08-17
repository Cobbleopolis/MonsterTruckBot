package twitch

import javax.inject.{Inject, Singleton}

import twitch.api.TwitchCommand
import twitch.commands.{TwitchBitsCommand, TwitchPingCommand, TwitchSubCountCommand, TwitchVersionCommand}

@Singleton
class TwitchCommandRegistry @Inject()(
                                        val pingCommand: TwitchPingCommand,
                                        val versionCommand: TwitchVersionCommand,
                                        val subCountCommand: TwitchSubCountCommand,
                                        val bitsCommand: TwitchBitsCommand
                                     ) {

    val commands: Map[String, TwitchCommand] = Map(
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        subCountCommand.name -> subCountCommand,
        bitsCommand.name -> bitsCommand
    )

}
