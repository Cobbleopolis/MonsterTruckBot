package discord

import javax.inject.{Inject, Singleton}

import discord.api.DiscordCommand
import discord.commands._

@Singleton
class DiscordCommandRegistry @Inject()(
                                          val helpCommand: DiscordHelpCommand,
                                          val pingCommand: DiscordPingCommand,
                                          val versionCommand: DiscordVersionCommand,
                                          val sosCommand: DiscordSoSCommand,
                                          val subCountCommand: DiscordSubCountCommand,
                                          val uptimeCommand: DiscordUptimeCommand,
                                          val bitsCommand: DiscordBitsCommand
                                      ) {

    val commands: Map[String, DiscordCommand] = Map(
        helpCommand.name -> helpCommand,
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        sosCommand.name -> sosCommand,
        subCountCommand.name -> subCountCommand,
        uptimeCommand.name -> uptimeCommand,
        bitsCommand.name -> bitsCommand
    )

}
