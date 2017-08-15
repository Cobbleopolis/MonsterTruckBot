package com.cobble.bot.common.util

import javax.inject.Inject

import com.cobble.bot.common.bitTracking.{JackshotsMode, NipDipMode, RBGMode}
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.models.bitTrackingFormData.BitTrackingFormData
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

class BitTrackingUtil @Inject()(implicit mtrConfigRef: MtrConfigRef, db: Database, cache: SyncCacheApi, val nipDipMode: NipDipMode, val jackshotsMode: JackshotsMode, val rbgMode: RBGMode) {

    def getBitTrackingFormData: BitTrackingFormData = {
        val bitTrackingSettings: BitTrackingSettings = BitTrackingSettings.get(mtrConfigRef.guildId).getOrElse(BitTrackingSettings(mtrConfigRef.guildId))
        BitTrackingFormData(
            mtrConfigRef.guildId,
            bitTrackingSettings.currentMode,
            nipDipMode.getCollectiveModeFormData(bitTrackingSettings.nipDipTemplate),
            rbgMode.getRBGFormData(bitTrackingSettings.rbgTemplate),
            jackshotsMode.getCollectiveModeFormData(bitTrackingSettings.jackshotsTemplate)
        )
    }

    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
        nipDipMode.setFromCollectiveModeFormData(bitTrackingFormData.nipDipFormData)
        jackshotsMode.setFromCollectiveModeFormData(bitTrackingFormData.jackshotsFormData)
    }

}
