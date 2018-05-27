package twitch

import common.DefaultLang
import common.api.TwitchChannelInfo
import common.api.bitTracking.{BitTrackingMode, BitTrackingState}
import common.ref.MtrConfigRef
import common.util.MessageFormatUtil
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.i18n.MessagesApi
import twitch.events.TwitchCheerEvent
import twitch.util.TwitchMessageUtil

class TwitchBotCheerEventHandler @Inject()(
                                              implicit bitTrackingState: BitTrackingState,
                                              messages: MessagesApi,
                                              twitchMessageUtil: TwitchMessageUtil,
                                              mtrConfigRef: MtrConfigRef,
                                              database: Database,
                                              cache: SyncCacheApi
                                          ) extends DefaultLang {

    def handleEvent(cheerEvent: TwitchCheerEvent): Unit = {
        if (!bitTrackingState.getIsPaused) {
            val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(cheerEvent.channelName)
            if (twitchChannelInfoOpt.isDefined)
                bitTrackingState.getCurrentBitTrackingMode match {
                    case BitTrackingMode.SINGLE_CHEER => handleSingleCheerTrackingMode(cheerEvent, twitchChannelInfoOpt.get)
                    case BitTrackingMode.COLLECTIVE => handleCollectiveTrackingMode(cheerEvent, twitchChannelInfoOpt.get)
                    case _ =>
                }
            else
                twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, cheerEvent.channelName, "error.twitch.channelDoesNotExist")
        }
    }

    def handleSingleCheerTrackingMode(cheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo): Unit = {

        if (cheerEvent.getCheerAmount >= bitTrackingState.getGoalAmount) {
            val goalDelta: Int = cheerEvent.getCheerAmount / bitTrackingState.getGoalAmount * bitTrackingState.getIncrementAmount
            bitTrackingState.addToGoalCount(goalDelta)
            twitchMessageUtil.replyToMessage(
                cheerEvent.getMessageEvent,
                twitchChannelInfo.name,
                MessageFormatUtil.formatVariableMessage(
                    bitTrackingState.getGoalMessage,
                    bitTrackingState.getGoalMessageVariables(goalDelta)
                )
            )
            bitTrackingState.addToGoalAmount(bitTrackingState.getGoalIncrementAmount * goalDelta)
        }
    }

    def handleCollectiveTrackingMode(cheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo): Unit = {
        bitTrackingState.addToNextGoal(cheerEvent.getCheerAmount)

        if (bitTrackingState.getToNextGoal >= bitTrackingState.getGoalAmount) {
            val goalDelta: Int = bitTrackingState.getToNextGoal / bitTrackingState.getGoalAmount * bitTrackingState.getIncrementAmount
            bitTrackingState.addToGoalCount(goalDelta)
            bitTrackingState.setToNextGoal(bitTrackingState.getToNextGoal % bitTrackingState.getGoalAmount)
            twitchMessageUtil.replyToMessage(
                cheerEvent.getMessageEvent,
                twitchChannelInfo.name,
                MessageFormatUtil.formatVariableMessage(
                    bitTrackingState.getGoalMessage,
                    bitTrackingState.getGoalMessageVariables(goalDelta)
                )
            )
            bitTrackingState.addToGoalAmount(bitTrackingState.getGoalIncrementAmount * goalDelta)
        }
    }

}
