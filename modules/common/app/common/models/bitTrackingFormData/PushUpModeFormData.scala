package common.models.bitTrackingFormData

import common.api.bitTracking.BitTrackingMode
import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.ref.BitTrackingRef


case class PushUpModeFormData(template: String = "",
                              currentMode: String = BitTrackingMode.COLLECTIVE.toString,
                              goalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                              toNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                              pushUpSetAmount: Int = BitTrackingRef.PushUpMode.DEFAULT_PUSH_UP_SET_AMOUNT,
                              goalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT
                             ) {

    def getCurrentCheerMode: BitTrackingMode = BitTrackingMode.withName(currentMode)
}
