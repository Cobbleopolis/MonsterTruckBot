package discord

import javax.inject.{Inject, Singleton}

import discord.api.DiscordCommand
import discord.commands.{DiscordPingCommand, DiscordSoSCommand, DiscordVersionCommand}

@Singleton
class DiscordCommandRegistry @Inject()(
                                          val pingCommand: DiscordPingCommand,
                                          val versionCommand: DiscordVersionCommand,
                                          val soSCommand: DiscordSoSCommand
                                      ) {

    val commands: Map[String, DiscordCommand] = Map(
        pingCommand.name -> pingCommand,
        versionCommand.name -> versionCommand,
        soSCommand.name -> soSCommand
    )

}
