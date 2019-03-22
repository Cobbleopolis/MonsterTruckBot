package discord.commands

import common.api.commands.PingCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject
import reactor.core.scala.publisher.Mono

class DiscordPingCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with PingCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Mono[Message] = messageUtil.reply("bot.ping")

}
