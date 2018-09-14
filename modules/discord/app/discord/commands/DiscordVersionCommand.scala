package discord.commands

import buildinfo.BuildInfo
import common.api.commands.VersionCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject

class DiscordVersionCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with VersionCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = messageUtil.reply(BuildInfo.version)

}
