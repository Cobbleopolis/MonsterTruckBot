package com.cobble.bot.common.ref

object BitTrackingRef {

    val BASE_CACHE_LOCATION: String = "bitTracking"

    val GOAL_AMOUNT_SUFFIX: String = "goalAmount"

    def getGoalAmountLocation(domain: String): String = s"$BASE_CACHE_LOCATION.$domain.$GOAL_AMOUNT_SUFFIX"

    val DEFAULT_GOAL_AMOUNT: Int = 10000

    val TO_NEXT_GOAL_SUFFIX: String = "toNextGoal"

    def getToNextGoalLocation(domain: String): String = s"$BASE_CACHE_LOCATION.$domain.$TO_NEXT_GOAL_SUFFIX"

    val DEFAULT_TO_NEXT_GOAL: Int = 0

    val GOAL_COUNT_SUFFIX: String = "goalCount"

    def getGoalCountLocation(domain: String): String = s"$BASE_CACHE_LOCATION.$domain.$GOAL_COUNT_SUFFIX"

    val DEFAULT_GOAL_COUNT: Int = 0

}
