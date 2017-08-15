package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.models.BitTrackingSettings

case class BitTrackingFormData(
                                  guildId: Long,
                                  currentMode: Int,
                                  nipDipFormData: CollectiveModeFormData,
                                  rbgFormData: RBGModeFormData,
                                  jackshotsFormData: CollectiveModeFormData
                              ) {
    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
        guildId = guildId,
        currentMode = currentMode,
        nipDipTemplate = nipDipFormData.template,
        rbgTemplate = rbgFormData.template,
        jackshotsTemplate = jackshotsFormData.template
    )
}
