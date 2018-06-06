package twitch

import javax.inject.{Inject, Singleton}

import twitch.api.TwitchCommand
import twitch.commands._

@Singleton
class TwitchCommandRegistry @Inject()(
                                         val helpCommand: TwitchHelpCommand,
                                         val pingCommand: TwitchPingCommand,
                                         val versionCommand: TwitchVersionCommand,
                                         val subCountCommand: TwitchSubCountCommand,
                                         val uptimeCommand: TwitchUptimeCommand,
                                         val bitsCommand: TwitchBitsCommand,
                                         val bitTestCommand: TwitchBitTestCommand,
                                         val bitGameCommand: TwitchBitGameCommand
                                     ) {

    val commands: Map[String, TwitchCommand] = Map(
        helpCommand.name -> helpCommand,
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        subCountCommand.name -> subCountCommand,
        uptimeCommand.name -> uptimeCommand,
        bitsCommand.name -> bitsCommand,
        bitTestCommand.name -> bitTestCommand,
        bitGameCommand.name -> bitGameCommand
    )

}
