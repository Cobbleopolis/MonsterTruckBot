package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.api.{BitTrackingMode, Command, PermissionLevel}
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.util.BitTrackingUtil

trait BitsCommand extends Command {

    override val name: String = "bits"

    override val permissionLevel: PermissionLevel = PermissionLevel.SUBSCRIBERS

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
        val variables: Map[String, String] = bitTrackingSettings.getCurrentMode match {
            case BitTrackingMode.NIP_DIP => bitTrackingUtil.nipDipMode.getFormattingVariables
            case _ => Map()
        }
        variables.foreach(kv => responseString = responseString.replaceAll(s"\\{${kv._1}\\}", kv._2))
        responseString
    }

}
