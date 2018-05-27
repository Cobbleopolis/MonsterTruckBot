package twitch

import javax.inject.Inject

import common.DefaultLang
//import common.api.bitTracking.{BitTrackingGame, CollectiveBitGameMode, BitTrackingMode, SingleCheerBitGameMode}
import common.api.TwitchChannelInfo
//import common.bitTracking.{PushUpMode, RBGMode}
import common.models.BitTrackingSettings
import common.ref.MtrConfigRef
import common.util.BitTrackingUtil
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
//        if (!bitTrackingUtil.commonBitTracking.getPaused) {
//            val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
//            val twitchChannelInfoOpt: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.get(cheerEvent.channelName)
//            if (twitchChannelInfoOpt.isDefined)
//                if (bitTrackingSettingsOpt.isDefined)
//                    bitTrackingSettingsOpt.get.getCurrentMode match {
//                        case BitTrackingGame.NIP_DIP => handleCollectiveGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.nipDipMode)
//                        case BitTrackingGame.RBG => handleRBGGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.rbgMode)
//                        case BitTrackingGame.JACKSHOTS => handleCollectiveGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.jackshotsMode)
//                        case BitTrackingGame.PUSH_UP => handlePushUpGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.pushUpMode)
//                        case BitTrackingGame.SING_IT_OR_SLAM_IT => handleSingleCheerGameMode(cheerEvent, twitchChannelInfoOpt.get, bitTrackingUtil.singItOrSlamItMode)
//                        case BitTrackingGame.NONE =>
//                        case _ =>
//                    }
//                else
//                    twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, cheerEvent.channelName, "error.twitch.channelDoesNotExist")
//        }
    }

//    def handleCollectiveGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, collectiveGameMode: CollectiveBitGameMode): Unit = {
//        collectiveGameMode.addToToNextGoalAmount(cheerEvent.getCheerAmount)
//
//        if (collectiveGameMode.getToNextGoal >= collectiveGameMode.getGoalAmount) {
//            val goalDelta: Int = collectiveGameMode.getToNextGoal / collectiveGameMode.getGoalAmount
//            collectiveGameMode.addToGoalCount(goalDelta)
//            collectiveGameMode.setToNextGoal(collectiveGameMode.getToNextGoal % collectiveGameMode.getGoalAmount)
//            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${collectiveGameMode.domain}", goalDelta)
//        }
//    }
//
//    def handleSingleCheerGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, singleCheerBitGameMode: SingleCheerBitGameMode): Unit = {
//        if (cheerEvent.getCheerAmount >= singleCheerBitGameMode.getGoalCount) {
//            val goalDelta: Int = cheerEvent.getCheerAmount / singleCheerBitGameMode.getGoalAmount
//            singleCheerBitGameMode.addToGoalCount(goalDelta)
//            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.${singleCheerBitGameMode.domain}", goalDelta)
//        }
//    }
//
//    def handleRBGGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, rbgMode: RBGMode): Unit = {
//        if (cheerEvent.getCheerAmount >= rbgMode.getRedShotAmount) {
//            rbgMode.addToRedShotCount(1)
//            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.red").toLowerCase)
//        } else if (cheerEvent.getCheerAmount >= rbgMode.getBlueShotAmount) {
//            rbgMode.addToBlueShotCount(1)
//            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.blue").toLowerCase)
//        } else if (cheerEvent.getCheerAmount >= rbgMode.getGreenShotAmount) {
//            rbgMode.addToGreenShotCount(1)
//            twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, "bot.bitTracking.event.rbg", messages("bot.bitTracking.rbg.shotTypes.green").toLowerCase)
//        }
//    }
//
//    def handlePushUpGameMode(cheerEvent: TwitchCheerEvent, channelInfo: TwitchChannelInfo, pushUpMode: PushUpMode): Unit = {
//        pushUpMode.getCheerMode match {
//            case BitTrackingMode.COLLECTIVE =>
//                pushUpMode.addToToNextGoalAmount(cheerEvent.getCheerAmount)
//                if(pushUpMode.getToNextGoal >= pushUpMode.getGoalAmount) {
//                    val goalDelta: Int = cheerEvent.getCheerAmount / pushUpMode.getGoalAmount
//                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
//                    pushUpMode.setToNextGoal(pushUpMode.getToNextGoal % pushUpMode.getGoalAmount)
//                    twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp", goalDelta, pushUpMode.getPushSetUpAmount)
//                }
//            case BitTrackingMode.SINGLE_CHEER =>
//                if(cheerEvent.getCheerAmount >= pushUpMode.getGoalAmount) {
//                    val goalDelta: Int = cheerEvent.getCheerAmount / pushUpMode.getGoalAmount
//                    pushUpMode.addToGoalCount(pushUpMode.getPushSetUpAmount * goalDelta)
//                    twitchMessageUtil.replyToMessage(cheerEvent.getMessageEvent, channelInfo.name, s"bot.bitTracking.event.pushUp", goalDelta, pushUpMode.getPushSetUpAmount)
//                }
//        }
//    }

}
