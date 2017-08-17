package twitch

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.bitTracking.{BitTrackingMode, CollectiveBitGameMode, GameCheerMode, SingleCheerBitGameMode}
import com.cobble.bot.common.api.TwitchChannelInfo
import com.cobble.bot.common.bitTracking.{PushUpMode, RBGMode}
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
        val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(twitchCheerEvent.channelName)
        if (twitchChannelInfoOpt.isDefined)
            if (bitTrackingSettingsOpt.isDefined)
                bitTrackingSettingsOpt.get.getCurrentMode match {
                    case BitTrackingMode.NIP_DIP => handleCollectiveGameMode(twitchCheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.nipDipMode)
                    case BitTrackingMode.RBG => handleRBGGameMode(twitchCheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.rbgMode)
                    case BitTrackingMode.JACKSHOTS => handleCollectiveGameMode(twitchCheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.jackshotsMode)
                    case BitTrackingMode.PUSH_UP => handlePushUpGameMode(twitchCheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.pushUpMode)
                    case BitTrackingMode.SING_IT_OR_SLAM_IT => handleSingleCheerGameMode(twitchCheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.singItOrSlamItMode)
                }
        else
            twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchCheerEvent.channelName, "error.twitch.channelDoesNotExist")
    }

    def handleCollectiveGameMode(twitchCheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo, collectiveGameMode: CollectiveBitGameMode): Unit = {
        collectiveGameMode.addToToNextGoalAmount(twitchCheerEvent.getCheerAmount)

        if (collectiveGameMode.getToNextGoal >= collectiveGameMode.getGoalAmount) {
            val goalDelta: Int = collectiveGameMode.getToNextGoal / collectiveGameMode.getGoalAmount
            collectiveGameMode.addToGoalCount(goalDelta)
            collectiveGameMode.setToNextGoal(collectiveGameMode.getToNextGoal % collectiveGameMode.getGoalAmount)

            if (goalDelta > 1)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.${collectiveGameMode.domain}.multiple", goalDelta)
            else
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.${collectiveGameMode.domain}.single")
        }
    }

    def handleSingleCheerGameMode(twitchCheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo, singleCheerBitGameMode: SingleCheerBitGameMode): Unit = {
        if (twitchCheerEvent.getCheerAmount >= singleCheerBitGameMode.getGoalCount) {
            val goalDelta: Int = twitchCheerEvent.getCheerAmount / singleCheerBitGameMode.getGoalAmount
            singleCheerBitGameMode.addToGoalCount(goalDelta)
            if (goalDelta > 1)
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.${singleCheerBitGameMode.domain}.multiple", goalDelta)
            else
                twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.${singleCheerBitGameMode.domain}.single")
        }
    }

    def handleRBGGameMode(twitchCheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo, rbgMode: RBGMode): Unit = {
        if (twitchCheerEvent.getCheerAmount >= rbgMode.getRedShotAmount) {
            rbgMode.addToRedShotCount(1)
            twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.red").toLowerCase)
        } else if (twitchCheerEvent.getCheerAmount >= rbgMode.getBlueShotAmount) {
            rbgMode.addToBlueShotCount(1)
            twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.blue").toLowerCase)
        } else if (twitchCheerEvent.getCheerAmount >= rbgMode.getGreenShotAmount) {
            rbgMode.addToGreenShotCount(1)
            twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.green").toLowerCase)
        }
    }

    def handlePushUpGameMode(twitchCheerEvent: TwitchCheerEvent, twitchChannelInfo: TwitchChannelInfo, pushUpMode: PushUpMode): Unit = {
        pushUpMode.getCheerMode match {
            case GameCheerMode.COLLECTIVE =>
                pushUpMode.addToToNextGoalAmount(twitchCheerEvent.getCheerAmount)
                if(pushUpMode.getToNextGoal >= pushUpMode.getGoalAmount) {
                    val goalDelta: Int = twitchCheerEvent.getCheerAmount / pushUpMode.getToNextGoal
                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
                    pushUpMode.setToNextGoal(pushUpMode.getToNextGoal % pushUpMode.getGoalAmount)
                    if (goalDelta > 1)
                        twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.pushUp.multiple", goalDelta, pushUpMode.getPushSetUpAmount)
                    else
                        twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.pushUp.single", pushUpMode.getPushSetUpAmount)
                }
            case GameCheerMode.SINGLE_CHEER =>
                if(twitchCheerEvent.getCheerAmount >= pushUpMode.getGoalAmount) {
                    val goalDelta: Int = twitchCheerEvent.getCheerAmount / pushUpMode.getToNextGoal
                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
                    if (goalDelta > 1)
                        twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.pushUp.multiple", goalDelta, pushUpMode.getPushSetUpAmount)
                    else
                        twitchMessageUtil.replyToMessage(twitchCheerEvent.getMessageEvent, twitchChannelInfo.displayName, s"bot.bitTracking.event.pushUp.single", pushUpMode.getPushSetUpAmount)
                }
        }
    }

}
