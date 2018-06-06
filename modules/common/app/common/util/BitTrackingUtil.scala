package common.util

import javax.inject.Inject

//import common.bitTracking._
import common.models.BitTrackingSettings
import common.models.bitTrackingFormData.BitTrackingFormData
import common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

class BitTrackingUtil @Inject()(implicit mtrConfigRef: MtrConfigRef,
                                db: Database,
                                cache: SyncCacheApi,
//                                val commonBitTracking: CommonBitTrackingSettings,
//                                val nipDipMode: NipDipMode,
//                                val jackshotsMode: JackshotsMode,
//                                val rbgMode: RBGMode,
//                                val pushUpMode: PushUpMode,
//                                val singItOrSlamItMode: SingItOrSlamItMode
                               ) {

//    def getBitTrackingFormData: BitTrackingFormData = {
//        val bitTrackingSettings: BitTrackingSettings = BitTrackingSettings.get(mtrConfigRef.guildId).getOrElse(BitTrackingSettings(mtrConfigRef.guildId))
//        BitTrackingFormData(
//            mtrConfigRef.guildId,
//            bitTrackingSettings.currentMode,
//            commonBitTracking.getCommonBitTrackingFormData,
//            nipDipMode.getCollectiveModeFormData(bitTrackingSettings.nipDipTemplate),
//            rbgMode.getRBGFormData(bitTrackingSettings.rbgTemplate),
//            jackshotsMode.getCollectiveModeFormData(bitTrackingSettings.jackshotsTemplate),
//            pushUpMode.getPushUpFormData(bitTrackingSettings.pushUpTemplate),
//            singItOrSlamItMode.getSingItOrSlamItFormData(bitTrackingSettings.singItOrSlamItTemplate)
//        )
//    }
//
//    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
//        commonBitTracking.setFromCommonBitTrackingFormData(bitTrackingFormData.commonBitTrackingFormData)
//        nipDipMode.setFromCollectiveModeFormData(bitTrackingFormData.nipDipFormData)
//        rbgMode.setFromRBGFormData(bitTrackingFormData.rbgFormData)
//        jackshotsMode.setFromCollectiveModeFormData(bitTrackingFormData.jackshotsFormData)
//        pushUpMode.setFromPushUpFormData(bitTrackingFormData.pushUpModeFormData)
//        singItOrSlamItMode.setFromSingItOrSlamItFormData(bitTrackingFormData.singItOrSlamItModeFormData)
//    }

}
