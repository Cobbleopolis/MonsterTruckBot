package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.models.BitTrackingSettings

case class BitTrackingFormData(
                                  guildId: Long,
                                  currentMode: Int,
                                  //Nip Dip
                                  nipDipFormData: CollectiveModeFormData,
                                  //RBG
                                  //Jackshots
                                  jackshotsFormData: CollectiveModeFormData
                              ) {
    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
        guildId = guildId,
        currentMode = currentMode,
        nipDipTemplate = nipDipFormData.template,
        jackshotsTemplate = jackshotsFormData.template
    )
}
