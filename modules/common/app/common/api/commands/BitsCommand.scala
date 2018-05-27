package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.bitTracking.BitTrackingGame
import common.api.{Command, PermissionLevel}
import common.models.BitTrackingSettings
import common.util.BitTrackingUtil

import scala.collection.mutable

trait BitsCommand extends Command {

    override val name: String = "bits"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

    def getFormattedResponse(bitTrackingSettings: BitTrackingSettings, bitTrackingUtil: BitTrackingUtil): String = {
//        var responseString: String = bitTrackingSettings.getCurrentMode match {
//            case BitTrackingGame.NONE => "bot.bitTracking.noCurrentBitTracking"
//            case BitTrackingGame.NIP_DIP => bitTrackingSettings.nipDipTemplate
//            case BitTrackingGame.RBG => bitTrackingSettings.rbgTemplate
//            case BitTrackingGame.JACKSHOTS => bitTrackingSettings.jackshotsTemplate
//            case BitTrackingGame.PUSH_UP => bitTrackingSettings.pushUpTemplate
//            case BitTrackingGame.SING_IT_OR_SLAM_IT => bitTrackingSettings.singItOrSlamItTemplate
//            case _ => "bot.bitTracking.unknownBitTrackingMode"
//        }
//        val variables: mutable.LinkedHashMap[String, String] = bitTrackingSettings.getCurrentMode match {
//            case BitTrackingGame.NIP_DIP => bitTrackingUtil.nipDipMode.getFormattingVariables
//            case BitTrackingGame.RBG => bitTrackingUtil.rbgMode.getFormattingVariables
//            case BitTrackingGame.JACKSHOTS => bitTrackingUtil.jackshotsMode.getFormattingVariables
//            case BitTrackingGame.PUSH_UP => bitTrackingUtil.pushUpMode.getFormattingVariables
//            case BitTrackingGame.SING_IT_OR_SLAM_IT => bitTrackingUtil.singItOrSlamItMode.getFormattingVariables
//            case _ => mutable.LinkedHashMap()
//        }
//        variables.foreach(kv => responseString = responseString.replaceAll(s"\\{${kv._1}\\}", kv._2))
//        responseString
        "N/A"
    }

}
