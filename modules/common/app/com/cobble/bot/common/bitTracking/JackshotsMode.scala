package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.bitTracking.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.api.bitTracking.{BitTrackingMode, CollectiveBitGameMode}
import play.api.cache.SyncCacheApi

class JackshotsMode @Inject()(val cache: SyncCacheApi) extends CollectiveBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.JACKSHOTS

}
