package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.ref.BitTrackingRef

case class CollectiveModeFormData (template: String = "",
                                   goalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                                   toNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                                   goalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT) extends GameModeFormData {

}
