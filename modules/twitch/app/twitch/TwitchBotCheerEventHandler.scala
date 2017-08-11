package twitch

import javax.inject.Inject

import com.cobble.bot.common.api.{BitTrackingMode, TwitchChannelInfo}
import com.cobble.bot.common.bitTracking.{BasicGameMode, NipDipMode}
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.BitTrackingUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import twitch.events.TwitchCheerEvent
import twitch.util.TwitchMessageUtil

class TwitchBotCheerEventHandler @Inject()(
                                              implicit bitTrackingUtil: BitTrackingUtil,
                                              twitchMessageUtil: TwitchMessageUtil,
                                              mtrConfigRef: MtrConfigRef,
                                              database: Database,
                                              cache: SyncCacheApi, nipDipMode: NipDipMode
                                          ) {

    def handleEvent(twitchCheerEvent: TwitchCheerEvent): Unit = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            bitTrackingSettingsOpt.get.getCurrentMode match {
                case BitTrackingMode.NIP_DIP => handleBasicGameMode(twitchCheerEvent, nipDipMode)
            }
    }

    def handleBasicGameMode(twitchCheerEvent: TwitchCheerEvent, basicGameMode: BasicGameMode): Unit = {
        basicGameMode.addToToNextGoalAmount(twitchCheerEvent.getCheerAmount)

        if (basicGameMode.getToNextGoal >= basicGameMode.getGoalAmount) {

            basicGameMode.addToGoalCount(basicGameMode.getToNextGoal / basicGameMode.getGoalAmount)
            basicGameMode.setToNextGoal(basicGameMode.getToNextGoal % basicGameMode.getGoalAmount)

            val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(twitchCheerEvent.channelName)
            if (twitchChannelInfoOpt.isDefined)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfoOpt.get.displayName, s"bot.bitTracking.event.${basicGameMode.domain}")
            else
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchCheerEvent.channelName, "error.twitch.channelDoesNotExist")
        }
    }

}
