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

    def handleEvent(cheerEvent: TwitchCheerEvent): Unit = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(cheerEvent.channelName)
        if (twitchChannelInfoOpt.isDefined)
            if (bitTrackingSettingsOpt.isDefined)
                bitTrackingSettingsOpt.get.getCurrentMode match {
                    case BitTrackingMode.NIP_DIP => handleCollectiveGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.nipDipMode)
                    case BitTrackingMode.RBG => handleRBGGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.rbgMode)
                    case BitTrackingMode.JACKSHOTS => handleCollectiveGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.jackshotsMode)
                    case BitTrackingMode.PUSH_UP => handlePushUpGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.pushUpMode)
                    case BitTrackingMode.SING_IT_OR_SLAM_IT => handleSingleCheerGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.singItOrSlamItMode)
                }
        else
            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, cheerEvent.channelName, "error.twitch.channelDoesNotExist")
    }

    def handleCollectiveGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, collectiveGameMode: CollectiveBitGameMode): Unit = {
        collectiveGameMode.addToToNextGoalAmount(cheerEvent.getCheerAmount)

        if (collectiveGameMode.getToNextGoal >= collectiveGameMode.getGoalAmount) {
            val goalDelta: Int = collectiveGameMode.getToNextGoal / collectiveGameMode.getGoalAmount
            collectiveGameMode.addToGoalCount(goalDelta)
            collectiveGameMode.setToNextGoal(collectiveGameMode.getToNextGoal % collectiveGameMode.getGoalAmount)

            if (goalDelta > 1)
                twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${collectiveGameMode.domain}.multiple", goalDelta)
            else
                twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${collectiveGameMode.domain}.single")
        }
    }

    def handleSingleCheerGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, singleCheerBitGameMode: SingleCheerBitGameMode): Unit = {
        if (cheerEvent.getCheerAmount >= singleCheerBitGameMode.getGoalCount) {
            val goalDelta: Int = cheerEvent.getCheerAmount / singleCheerBitGameMode.getGoalAmount
            singleCheerBitGameMode.addToGoalCount(goalDelta)
            if (goalDelta > 1)
                twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${singleCheerBitGameMode.domain}.multiple", goalDelta)
            else
                twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${singleCheerBitGameMode.domain}.single")
        }
    }

    def handleRBGGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, rbgMode: RBGMode): Unit = {
        if (cheerEvent.getCheerAmount >= rbgMode.getRedShotAmount) {
            rbgMode.addToRedShotCount(1)
            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.red").toLowerCase)
        } else if (cheerEvent.getCheerAmount >= rbgMode.getBlueShotAmount) {
            rbgMode.addToBlueShotCount(1)
            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.blue").toLowerCase)
        } else if (cheerEvent.getCheerAmount >= rbgMode.getGreenShotAmount) {
            rbgMode.addToGreenShotCount(1)
            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg.single", messages("bot.bitTracking.rbg.shotTypes.green").toLowerCase)
        }
    }

    def handlePushUpGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, pushUpMode: PushUpMode): Unit = {
        pushUpMode.getCheerMode match {
            case GameCheerMode.COLLECTIVE =>
                pushUpMode.addToToNextGoalAmount(cheerEvent.getCheerAmount)
                if(pushUpMode.getToNextGoal >= pushUpMode.getGoalAmount) {
                    val goalDelta: Int = cheerEvent.getCheerAmount / pushUpMode.getToNextGoal
                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
                    pushUpMode.setToNextGoal(pushUpMode.getToNextGoal % pushUpMode.getGoalAmount)
                    if (goalDelta > 1)
                        twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp.multiple", goalDelta, pushUpMode.getPushSetUpAmount)
                    else
                        twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp.single", pushUpMode.getPushSetUpAmount)
                }
            case GameCheerMode.SINGLE_CHEER =>
                if(cheerEvent.getCheerAmount >= pushUpMode.getGoalAmount) {
                    val goalDelta: Int = cheerEvent.getCheerAmount / pushUpMode.getToNextGoal
                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
                    if (goalDelta > 1)
                        twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp.multiple", goalDelta, pushUpMode.getPushSetUpAmount)
                    else
                        twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp.single", pushUpMode.getPushSetUpAmount)
                }
        }
    }

}
