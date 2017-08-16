package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.api.bitTracking.GameCheerMode
import com.cobble.bot.common.api.bitTracking.GameCheerMode.GameCheerMode
import com.cobble.bot.common.ref.BitTrackingRef


case class PushUpModeFormData(template: String = "",
                              currentMode: String = GameCheerMode.COLLECTIVE.toString,
                              goalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                              toNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                              pushUpSetAmount: Int = BitTrackingRef.PushUpMode.DEFAULT_PUSH_UP_SET_AMOUNT,
                              goalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT
                             ) {

    def getCurrentCheerMode: GameCheerMode = GameCheerMode.withName(currentMode)
}
