package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.PingCommand
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.util.DiscordMessageUtil

class DiscordPingCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with PingCommand {

    override def execute(implicit event: CommandExecutionEvent): Unit = messageUtil.reply("bot.ping")

}
