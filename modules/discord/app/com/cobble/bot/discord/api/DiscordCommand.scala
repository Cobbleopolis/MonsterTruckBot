package com.cobble.bot.discord.api

import com.cobble.bot.common.api.Command
import com.cobble.bot.discord.event.CommandExecutionEvent
import com.cobble.bot.discord.util.DiscordMessageUtil

trait DiscordCommand extends Command {

    val messageUtil: DiscordMessageUtil

    def execute(implicit event: CommandExecutionEvent)

}
