package com.cobble.bot.common.models

import com.cobble.bot.common.ref.BitTrackingRef

case class BitTrackingFormData(
                                  totalBits: Int = BitTrackingRef.DEFAULT_TOTAL_BITS,
                                  goalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                                  toNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                                  goalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT
                              ) {

}
