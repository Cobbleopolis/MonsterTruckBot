package twitch.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitGameCommand
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchBitGameCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef, bitTrackingState: BitTrackingState) extends TwitchCommand with BitGameCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        messageUtil.sendMessageToChannel(executionEvent.getChannel, getFormattedResponse(bitTrackingState))
    }

}
