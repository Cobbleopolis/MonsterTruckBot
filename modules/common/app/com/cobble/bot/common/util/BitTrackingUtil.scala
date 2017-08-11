package com.cobble.bot.common.util

import javax.inject.Inject

import com.cobble.bot.common.bitTracking.{JackshotsMode, NipDipMode}
import com.cobble.bot.common.models.{BitTrackingFormData, BitTrackingSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

class BitTrackingUtil @Inject()(implicit mtrConfigRef: MtrConfigRef, db: Database, cache: SyncCacheApi, val nipDipMode: NipDipMode, val jackshotsMode: JackshotsMode) {

    def getBitTrackingFormData: BitTrackingFormData = {
        val bitTrackingSettings: BitTrackingSettings = BitTrackingSettings.get(mtrConfigRef.guildId).getOrElse(BitTrackingSettings(mtrConfigRef.guildId))
        BitTrackingFormData(
            mtrConfigRef.guildId,
            bitTrackingSettings.currentMode,
            //Nip Dip
            bitTrackingSettings.nipDipTemplate,
            nipDipMode.getGoalAmount,
            nipDipMode.getToNextGoal,
            nipDipMode.getGoalCount,
            //RBG
            //Jackshots
            bitTrackingSettings.jackshotsTemplate,
            jackshotsMode.getGoalAmount,
            jackshotsMode.getToNextGoal,
            jackshotsMode.getGoalCount
        )
    }

    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
        //Nip Dip
        nipDipMode.setGoalAmount(bitTrackingFormData.nipDipGoalAmount)
        nipDipMode.setToNextGoal(bitTrackingFormData.nipDipToNextGoal)
        nipDipMode.setGoalCount(bitTrackingFormData.nipDipGoalCount)
        //RBG
        //Jackshots
        jackshotsMode.setGoalAmount(bitTrackingFormData.jackshotsGoalAmount)
        jackshotsMode.setToNextGoal(bitTrackingFormData.jackshotsToNextGoal)
        jackshotsMode.setGoalCount(bitTrackingFormData.jackshotsGoalCount)
    }

}
