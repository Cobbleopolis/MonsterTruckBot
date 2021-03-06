package discord.commands

import common.api.commands.PingCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject

class DiscordPingCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with PingCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = messageUtil.reply("bot.ping")

}
