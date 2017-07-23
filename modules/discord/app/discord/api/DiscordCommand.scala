package discord.api

import com.cobble.bot.common.api.Command
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil

trait DiscordCommand extends Command {

    val messageUtil: DiscordMessageUtil

    def execute(implicit event: DiscordCommandExecutionEvent): Unit

}
