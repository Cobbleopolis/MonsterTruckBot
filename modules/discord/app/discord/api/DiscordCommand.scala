package discord.api

import common.api.Command
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import org.reactivestreams.Publisher
import reactor.core.scala.publisher.Mono

trait DiscordCommand extends Command {

    val messageUtil: DiscordMessageUtil

    def execute(implicit event: DiscordCommandExecutionEvent): Publisher[_]

}
