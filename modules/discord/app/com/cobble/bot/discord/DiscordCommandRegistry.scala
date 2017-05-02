package com.cobble.bot.discord

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.api.commands.PingCommand
import com.cobble.bot.discord.api.DiscordCommand
import com.cobble.bot.discord.commands.DiscordPingCommand

@Singleton
class DiscordCommandRegistry @Inject()(
                                          discordPingCommand: DiscordPingCommand
                                      ) {

    val commands: Map[String, DiscordCommand] = Map(
        discordPingCommand.name -> discordPingCommand
    )

    val pingCommand: PingCommand = discordPingCommand

}
