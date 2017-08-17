package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.ref.BitTrackingRef

case class SingItOrSlamItModeFormData(template: String = "",
                                      goalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                                      goalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT,
                                      roundsWon: Int = BitTrackingRef.SingItOrSlamIt.DEFAULT_ROUNDS_WON,
                                      roundsLost: Int = BitTrackingRef.SingItOrSlamIt.DEFAULT_ROUNDS_LOST
                                     ) {

}
