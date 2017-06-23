package discord

import javax.inject.{Inject, Singleton}

import discord.api.DiscordCommand
import discord.commands.{DiscordHelpCommand, DiscordPingCommand, DiscordSoSCommand, DiscordVersionCommand}

@Singleton
class DiscordCommandRegistry @Inject()(
                                          val helpCommand: DiscordHelpCommand,
                                          val pingCommand: DiscordPingCommand,
                                          val versionCommand: DiscordVersionCommand,
                                          val sosCommand: DiscordSoSCommand
                                      ) {

    val commands: Map[String, DiscordCommand] = Map(
        helpCommand.name -> helpCommand,
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        sosCommand.name -> sosCommand
    )

}
