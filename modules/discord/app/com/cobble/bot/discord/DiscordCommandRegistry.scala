package com.cobble.bot.discord

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.api.commands.{PingCommand, VersionCommand}
import com.cobble.bot.discord.api.DiscordCommand
import com.cobble.bot.discord.commands.{DiscordPingCommand, DiscordVersionCommand}

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
