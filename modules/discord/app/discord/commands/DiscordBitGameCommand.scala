package discord.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitGameCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject

class DiscordBitGameCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, bitTrackingState: BitTrackingState) extends DiscordCommand with BitGameCommand {

    override def execute(implicit executionEvent: DiscordCommandExecutionEvent): Unit = {
        messageUtil.replyNoAt(getFormattedResponse(bitTrackingState))
    }

}
