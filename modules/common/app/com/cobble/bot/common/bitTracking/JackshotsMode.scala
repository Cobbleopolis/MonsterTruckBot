package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.BitTrackingMode
import com.cobble.bot.common.api.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class JackshotsMode @Inject()(val cache: SyncCacheApi) extends CollectiveBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.JACKSHOTS

    override val defaultGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT

}
