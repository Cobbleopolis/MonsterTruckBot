package common.models.bitTrackingFormData

import common.api.bitTracking.BitTrackingMode
import common.api.bitTracking.BitTrackingMode.BitTrackingMode

case class BitTrackingFormData(
                                  bitTrackingMode: String = BitTrackingMode.COLLECTIVE.toString,
                                  isPaused: Boolean = false,
                                  gameMessage: String = "",
                                  bitsMessage: String = "",
                                  goalMessage: String = "",
                                  toNextGoal: Int = 0,
                                  goalAmount: Int = 10000,
                                  goalCount: Int = 0,
                                  incrementAmount: Int = 1,
                                  goalIncrementAmount: Int = 0
                              ) {

    def getBitTrackingMode: BitTrackingMode = BitTrackingMode.withName(bitTrackingMode)

    //    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
    //        guildId = guildId,
    //        currentMode = currentMode,
    //        nipDipTemplate = nipDipFormData.template,
    //        rbgTemplate = rbgFormData.template,
    //        jackshotsTemplate = jackshotsFormData.template,
    //        pushUpTemplate = pushUpModeFormData.template,
    //        singItOrSlamItTemplate = singItOrSlamItModeFormData.template
    //    )
}
