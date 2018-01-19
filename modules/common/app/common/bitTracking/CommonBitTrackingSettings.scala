package common.bitTracking

import javax.inject.Inject

import common.models.bitTrackingFormData.CommonBitTrackingFormData
import common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class CommonBitTrackingSettings @Inject()(cache: SyncCacheApi) {

    // Paused
    private val PAUSED_SUFFIX: String = "paused"

    private val PAUSED_LOCATION: String = BitTrackingRef.getCommonBitTrackingLocation(PAUSED_SUFFIX)

    def getCommonBitTrackingFormData: CommonBitTrackingFormData = CommonBitTrackingFormData(
        getPaused
    )

    // Paused
    def getPaused: Boolean = cache.get[Boolean](PAUSED_SUFFIX).getOrElse(false)

    def setFromCommonBitTrackingFormData(commonBitTrackingFormData: CommonBitTrackingFormData): Unit = {
        setPaused(commonBitTrackingFormData.paused)
    }

    def setPaused(paused: Boolean): Unit = cache.set(PAUSED_SUFFIX, paused)

}
