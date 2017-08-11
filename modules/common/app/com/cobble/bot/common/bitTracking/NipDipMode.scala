package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.BitTrackingMode
import com.cobble.bot.common.api.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class NipDipMode @Inject()(val cache: SyncCacheApi) extends BasicGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.NIP_DIP

    override val defaultGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT
}
