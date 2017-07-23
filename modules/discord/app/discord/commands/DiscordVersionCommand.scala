package discord.commands

import javax.inject.Inject

import buildinfo.BuildInfo
import com.cobble.bot.common.api.commands.VersionCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil

class DiscordVersionCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with VersionCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = messageUtil.reply(BuildInfo.version)

}
