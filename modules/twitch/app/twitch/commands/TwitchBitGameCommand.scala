package twitch.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitGameCommand
import javax.inject.Inject
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchBitGameCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, bitTrackingState: BitTrackingState) extends TwitchCommand with BitGameCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        messageUtil.sendMessageToChannel(executionEvent.getChannel, getFormattedResponse(bitTrackingState))
    }

}
