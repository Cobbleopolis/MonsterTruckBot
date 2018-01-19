package common.bitTracking

import javax.inject.Inject

import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.api.bitTracking.{BitTrackingMode, CollectiveBitGameMode}
import play.api.cache.SyncCacheApi

class NipDipMode @Inject()(val cache: SyncCacheApi) extends CollectiveBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.NIP_DIP

}
