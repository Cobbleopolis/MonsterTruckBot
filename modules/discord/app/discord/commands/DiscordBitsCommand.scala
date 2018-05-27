package discord.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitsCommand
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database

class DiscordBitsCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef, bitTrackingState: BitTrackingState) extends DiscordCommand with BitsCommand {

    override def execute(implicit executionEvent: DiscordCommandExecutionEvent): Unit = {
        messageUtil.replyNoAt(getFormattedResponse(bitTrackingState))
    }

}
