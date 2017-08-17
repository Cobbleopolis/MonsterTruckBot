package com.cobble.bot.common.models.bitTrackingFormData

import com.cobble.bot.common.ref.BitTrackingRef

case class RBGModeFormData(template: String = "",
                           greenShotAmount: Int = BitTrackingRef.BitCheerLevels.GREEN_BIT,
                           greenShotCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT,
                           blueShotAmount: Int = BitTrackingRef.BitCheerLevels.BLUE_BIT,
                           blueShotCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT,
                           redShotAmount: Int = BitTrackingRef.BitCheerLevels.RED_BIT,
                           redShotCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT
                          ) extends GameModeFormData {

}
