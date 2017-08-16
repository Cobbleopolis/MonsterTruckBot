package twitch

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.{BitTrackingMode, TwitchChannelInfo}
import com.cobble.bot.common.bitTracking.{CollectiveBitGameMode, RBGMode}
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.BitTrackingUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.i18n.MessagesApi
import twitch.events.TwitchCheerEvent
import twitch.util.TwitchMessageUtil

class TwitchBotCheerEventHandler @Inject()(
                                              implicit bitTrackingUtil: BitTrackingUtil,
                                              messages: MessagesApi,
                                              twitchMessageUtil: TwitchMessageUtil,
                                              mtrConfigRef: MtrConfigRef,
                                              database: Database,
                                              cache: SyncCacheApi
                                          ) extends DefaultLang {

    def handleEvent(twitchCheerEvent: TwitchCheerEvent): Unit = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            bitTrackingSettingsOpt.get.getCurrentMode match {
                case BitTrackingMode.NIP_DIP => handleBasicGameMode(twitchCheerEvent, bitTrackingUtil.nipDipMode)
                case BitTrackingMode.RBG => handleRBGGameMode(twitchCheerEvent, bitTrackingUtil.rbgMode)
                case BitTrackingMode.JACKSHOTS => handleBasicGameMode(twitchCheerEvent, bitTrackingUtil.jackshotsMode)
            }
    }

    def handleBasicGameMode(twitchCheerEvent: TwitchCheerEvent, basicGameMode: CollectiveBitGameMode): Unit = {
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

    def handleRBGGameMode(twitchCheerEvent: TwitchCheerEvent, rbgMode: RBGMode): Unit = {
        val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(twitchCheerEvent.channelName)
        if (twitchChannelInfoOpt.isDefined) {
            if (twitchCheerEvent.getCheerAmount >= rbgMode.getRedShotAmount) {
                rbgMode.addToRedShotCount(1)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfoOpt.get.displayName, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.red").toLowerCase)
            } else if (twitchCheerEvent.getCheerAmount >= rbgMode.getBlueShotAmount) {
                rbgMode.addToBlueShotCount(1)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfoOpt.get.displayName, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.blue").toLowerCase)
            } else if (twitchCheerEvent.getCheerAmount >= rbgMode.getGreenShotAmount) {
                rbgMode.addToGreenShotCount(1)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfoOpt.get.displayName, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.green").toLowerCase)
            }
        } else
            twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchCheerEvent.channelName, "error.twitch.channelDoesNotExist")
    }

}
