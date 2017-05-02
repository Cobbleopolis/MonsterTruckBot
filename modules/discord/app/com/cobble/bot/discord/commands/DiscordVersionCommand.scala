package com.cobble.bot.discord.commands

import javax.inject.Inject

import buildinfo.BuildInfo
import com.cobble.bot.common.api.commands.VersionCommand
import com.cobble.bot.discord.api.DiscordCommand
import com.cobble.bot.discord.event.CommandExecutionEvent
import com.cobble.bot.discord.util.DiscordMessageUtil

class DiscordVersionCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with VersionCommand {

    override def execute(implicit event: CommandExecutionEvent): Unit = messageUtil.reply(BuildInfo.version)

}
