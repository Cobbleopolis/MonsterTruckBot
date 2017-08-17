package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.api.bitTracking.BitTrackingMode
import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.util.BitTrackingUtil

import scala.collection.mutable

trait BitsCommand extends Command {

    override val name: String = "bits"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

    def getFormattedResponse(bitTrackingSettings: BitTrackingSettings, bitTrackingUtil: BitTrackingUtil): String = {
        var responseString: String = bitTrackingSettings.getCurrentMode match {
            case BitTrackingMode.NONE => "bot.bitTracking.noCurrentBitTracking"
            case BitTrackingMode.NIP_DIP => bitTrackingSettings.nipDipTemplate
            case BitTrackingMode.RBG => bitTrackingSettings.rbgTemplate
            case BitTrackingMode.JACKSHOTS => bitTrackingSettings.jackshotsTemplate
            case BitTrackingMode.PUSH_UP => bitTrackingSettings.pushUpTemplate
            case BitTrackingMode.SING_IT_OR_SLAM_IT => bitTrackingSettings.singItOrSlamItTemplate
            case _ => "bot.bitTracking.unknownBitTrackingMode"
        }
        val variables: mutable.LinkedHashMap[String, String] = bitTrackingSettings.getCurrentMode match {
            case BitTrackingMode.NIP_DIP => bitTrackingUtil.nipDipMode.getFormattingVariables
            case BitTrackingMode.RBG => bitTrackingUtil.rbgMode.getFormattingVariables
            case BitTrackingMode.JACKSHOTS => bitTrackingUtil.jackshotsMode.getFormattingVariables
            case BitTrackingMode.PUSH_UP => bitTrackingUtil.pushUpMode.getFormattingVariables
            case BitTrackingMode.SING_IT_OR_SLAM_IT => bitTrackingUtil.singItOrSlamItMode.getFormattingVariables
            case _ => mutable.LinkedHashMap()
        }
        variables.foreach(kv => responseString = responseString.replaceAll(s"\\{${kv._1}\\}", kv._2))
        responseString
    }

}
