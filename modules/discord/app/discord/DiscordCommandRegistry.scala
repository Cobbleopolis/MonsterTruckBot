package discord

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.api.commands.{PingCommand, VersionCommand}
import discord.api.DiscordCommand
import discord.commands.{DiscordPingCommand, DiscordVersionCommand}

@Singleton
class DiscordCommandRegistry @Inject()(
                                          discordPingCommand: DiscordPingCommand,
                                          discordVersionCommand: DiscordVersionCommand
                                      ) {

    val commands: Map[String, DiscordCommand] = Map(
        discordPingCommand.name -> discordPingCommand,
        discordVersionCommand.name -> discordVersionCommand
    )

    val pingCommand: PingCommand = discordPingCommand

    val versionCommand: VersionCommand = discordVersionCommand

}
