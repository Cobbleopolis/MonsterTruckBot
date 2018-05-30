package common.models.bitTrackingFormData

import common.api.bitTracking.BitTrackingMode
import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.models.BitTrackingSettings

case class BitTrackingFormData(
                                  guildId: Long,
                                  bitTrackingMode: Int = BitTrackingMode.COLLECTIVE.id,
                                  isPaused: Boolean = false,
                                  bitGameMessage: String = "",
                                  bitsMessage: String = "",
                                  goalMessage: String = "",
                                  toNextGoal: Int = 0,
                                  goalAmount: Int = 10000,
                                  goalCount: Int = 0,
                                  incrementAmount: Int = 1,
                                  goalIncrementAmount: Int = 0
                              ) {

    def getBitTrackingMode: BitTrackingMode = BitTrackingMode(bitTrackingMode)

    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
        guildId = guildId,
        currentMode = bitTrackingMode,
        bitGameMessage = bitGameMessage,
        bitsMessage = bitsMessage,
        goalMessage = goalMessage
    )
}
