package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.models.bitTrackingFormData.CommonBitTrackingFormData
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class CommonBitTrackingSettings @Inject()(cache: SyncCacheApi) {

    // Paused
    private val PAUSED_SUFFIX: String = "paused"

    private val PAUSED_LOCATION: String = BitTrackingRef.getCommonBitTrackingLocation(PAUSED_SUFFIX)

    // Paused
    def getPaused: Boolean = cache.get[Boolean](PAUSED_SUFFIX).getOrElse(false)

    def setPaused(paused: Boolean): Unit = cache.set(PAUSED_SUFFIX, paused)

    def getCommonBitTrackingFormData: CommonBitTrackingFormData = CommonBitTrackingFormData(
        getPaused
    )

    def setFromCommonBitTrackingFormData(commonBitTrackingFormData: CommonBitTrackingFormData): Unit = {
        setPaused(commonBitTrackingFormData.paused)
    }

}
