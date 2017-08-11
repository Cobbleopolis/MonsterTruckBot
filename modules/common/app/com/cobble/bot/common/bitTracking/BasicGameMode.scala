package com.cobble.bot.common.bitTracking

import com.cobble.bot.common.api.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

abstract class BasicGameMode {

    val cache: SyncCacheApi

    val mode: BitTrackingMode

    val defaultGoalAmount: Int

    val defaultToNextGoalAmount: Int = 0

    val defaultGoalCount: Int = 0

    val numberFormatString: String = "%,d"

    lazy val domain: String = mode.toString.toLowerCase()

    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.getGoalAmountLocation(domain)).getOrElse(defaultGoalAmount)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.getGoalAmountLocation(domain), goalAmount)


    def getToNextGoal: Int = cache.get[Int](BitTrackingRef.getToNextGoalLocation(domain)).getOrElse(defaultToNextGoalAmount)

    def setToNextGoal(toNextGoalAmount: Int): Unit = cache.set(BitTrackingRef.getToNextGoalLocation(domain), toNextGoalAmount)

    def addToToNextGoalAmount(delta: Int): Unit = setToNextGoal(getToNextGoal + delta)


    def getGoalCount: Int = cache.get[Int](BitTrackingRef.getGoalCountLocation(domain)).getOrElse(defaultGoalCount)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.getGoalCountLocation(domain), goalCount)

    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    def getFormattingVariables: Map[String, String] = Map (
        "goalAmount" -> numberFormatString.format(getGoalAmount),
        "toNextGoal" -> numberFormatString.format(getToNextGoal),
        "goalCount" -> numberFormatString.format(getGoalCount)
    )
}
