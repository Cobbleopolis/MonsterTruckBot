package com.cobble.bot.discord.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.PingCommand
import com.cobble.bot.discord.api.DiscordCommand
import com.cobble.bot.discord.event.CommandExecutionEvent
import com.cobble.bot.discord.util.DiscordMessageUtil

class DiscordPingCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with PingCommand {

    override def execute(implicit event: CommandExecutionEvent): Unit = messageUtil.reply("bot.ping")

}
