package twitch.commands

import common.api.bitTracking.BitTrackingState
import common.api.commands.BitsCommand
import javax.inject.Inject
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchBitsCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, bitTrackingState: BitTrackingState) extends TwitchCommand with BitsCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        messageUtil.sendMessageToChannel(executionEvent.getChannel, getFormattedResponse(bitTrackingState))
    }

}
