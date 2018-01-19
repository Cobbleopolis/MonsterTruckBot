package discord.api

import common.api.Command
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil

trait DiscordCommand extends Command {

    val messageUtil: DiscordMessageUtil

    def execute(implicit event: DiscordCommandExecutionEvent): Unit

}
