package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.bitTracking.BitTrackingState
import common.api.{Command, PermissionLevel}
import common.util.MessageFormatUtil

trait BitGameCommand extends Command {

    override val name: String = "bitgame"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

    def getFormattedResponse(bitTrackingState: BitTrackingState): String = {
        //        var responseString: String = bitTrackingState.getBitsMessage
        //        bitTrackingState.getFormattingVariables.foreach(kv => responseString = responseString.replaceAll(s"\\{${kv._1}\\}", kv._2))
        //        responseString
        MessageFormatUtil.formatVariableMessage(bitTrackingState.getGameMessage, bitTrackingState.getFormattingVariables)
    }

}
