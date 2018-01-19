package common.models.bitTrackingFormData

import common.models.BitTrackingSettings

case class BitTrackingFormData(
                                  guildId: Long,
                                  currentMode: Int,
                                  commonBitTrackingFormData: CommonBitTrackingFormData,
                                  nipDipFormData: CollectiveModeFormData,
                                  rbgFormData: RBGModeFormData,
                                  jackshotsFormData: CollectiveModeFormData,
                                  pushUpModeFormData: PushUpModeFormData,
                                  singItOrSlamItModeFormData: SingItOrSlamItModeFormData
                              ) {
    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
        guildId = guildId,
        currentMode = currentMode,
        nipDipTemplate = nipDipFormData.template,
        rbgTemplate = rbgFormData.template,
        jackshotsTemplate = jackshotsFormData.template,
        pushUpTemplate = pushUpModeFormData.template,
        singItOrSlamItTemplate = singItOrSlamItModeFormData.template
    )
}
