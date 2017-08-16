package com.cobble.bot.common.ref

object BitTrackingRef {

    val BASE_CACHE_LOCATION: String = "bitTracking"

    val GOAL_AMOUNT_SUFFIX: String = "goalAmount"

    def getGoalAmountLocation(domain: String): String = getBitTrackingLocation(domain, GOAL_AMOUNT_SUFFIX)

    val DEFAULT_GOAL_AMOUNT: Int = BitCheerLevels.RED_BIT

    val TO_NEXT_GOAL_SUFFIX: String = "toNextGoal"

    def getToNextGoalLocation(domain: String): String = getBitTrackingLocation(domain, TO_NEXT_GOAL_SUFFIX)

    val DEFAULT_TO_NEXT_GOAL: Int = 0

    val GOAL_COUNT_SUFFIX: String = "goalCount"

    def getGoalCountLocation(domain: String): String = getBitTrackingLocation(domain, GOAL_COUNT_SUFFIX)

    val DEFAULT_GOAL_COUNT: Int = 0

    def getBitTrackingLocation(domain: String, suffix: String): String = s"$BASE_CACHE_LOCATION.$domain.$suffix"

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

}
