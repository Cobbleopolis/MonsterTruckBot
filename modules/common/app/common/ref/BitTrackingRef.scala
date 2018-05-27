package common.ref

import common.api.bitTracking.BitTrackingMode
import common.api.bitTracking.BitTrackingMode.BitTrackingMode

object BitTrackingRef {

    val BASE_CACHE_LOCATION: String = "bitTracking"

    val BIT_TRACKING_MODE_SUFFIX: String = "bitTrackingMode"
    val DEFAULT_BIT_TRACKING_MODE: BitTrackingMode = BitTrackingMode.SINGLE_CHEER

    val IS_PAUSED_SUFFIX: String = "isPaused"
    val DEFAULT_IS_PAUSED: Boolean = false

    val GOAL_AMOUNT_SUFFIX: String = "goalAmount"
    val DEFAULT_GOAL_AMOUNT: Int = BitCheerLevels.RED_BIT

    val TO_NEXT_GOAL_SUFFIX: String = "toNextGoal"
    val DEFAULT_TO_NEXT_GOAL: Int = 0

    val GOAL_COUNT_SUFFIX: String = "goalCount"
    val DEFAULT_GOAL_COUNT: Int = 0

    def getBitTrackingLocation(suffix: String): String = s"$BASE_CACHE_LOCATION.$suffix"

    val BIT_TRACKING_MODE_LOCATION: String = getBitTrackingLocation(BIT_TRACKING_MODE_SUFFIX)

    val IS_PAUSED_LOCATION: String = getBitTrackingLocation(IS_PAUSED_SUFFIX)

    val GOAL_AMOUNT_LOCATION: String = getBitTrackingLocation(GOAL_AMOUNT_SUFFIX)

    val TO_NEXT_GOAL_LOCATION: String = getBitTrackingLocation(TO_NEXT_GOAL_SUFFIX)

    val GOAL_COUNT_LOCATION: String = getBitTrackingLocation(GOAL_COUNT_SUFFIX)

    def getCommonBitTrackingLocation(suffix: String): String = s"$BASE_CACHE_LOCATION.$suffix"

    object BitCheerLevels {

        val GREY_BIT: Int = 1

        val PURPLE_BIT: Int = 100

        val GREEN_BIT: Int = 1000

        val BLUE_BIT: Int = 5000

        val RED_BIT: Int = 10000

        val GOLD_BIT: Int = 100000
    }

    object PushUpMode {

        val DEFAULT_PUSH_UP_SET_AMOUNT: Int = 20

    }

    object SingItOrSlamIt {

        val DEFAULT_ROUNDS_WON: Int = 0

        val DEFAULT_ROUNDS_LOST: Int = 0

    }

}
