package twitch

import javax.inject.Inject

import com.cobble.bot.common.api.TwitchChannelInfo
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.BitTrackingUtil
import twitch.events.TwitchCheerEvent
import twitch.util.TwitchMessageUtil

class TwitchBotCheerEventHandler @Inject()(bitTrackingUtil: BitTrackingUtil, twitchMessageUtil: TwitchMessageUtil, mtrConfigRef: MtrConfigRef) {

    def handleEvent(twitchCheerEvent: TwitchCheerEvent): Unit = {
        bitTrackingUtil.addToTotalBits(twitchCheerEvent.getCheerAmount)
        bitTrackingUtil.addToToNextGoalAmount(twitchCheerEvent.getCheerAmount)

        if (bitTrackingUtil.getToNextGoal > bitTrackingUtil.getGoalAmount) {

            bitTrackingUtil.setToNextGoal(bitTrackingUtil.getToNextGoal % bitTrackingUtil.getGoalAmount)
            bitTrackingUtil.addToGoalCount(1)

            val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(twitchCheerEvent.channelName)
            if (twitchChannelInfoOpt.isDefined)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfoOpt.get.displayName, "bot.bitTracking.event.nipDip")
            else
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchCheerEvent.channelName, "error.twitch.channelDoesNotExist")
        }
    }

}
