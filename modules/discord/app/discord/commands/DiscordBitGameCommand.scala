package discord.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitGameCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject
import reactor.core.scala.publisher.Mono

class DiscordBitGameCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, bitTrackingState: BitTrackingState) extends DiscordCommand with BitGameCommand {

    override def execute(implicit executionEvent: DiscordCommandExecutionEvent): Mono[Message] = {
        messageUtil.replyNoAt(getFormattedResponse(bitTrackingState))
    }

}
