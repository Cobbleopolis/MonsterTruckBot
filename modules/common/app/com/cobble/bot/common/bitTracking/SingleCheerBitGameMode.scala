package com.cobble.bot.common.bitTracking

import com.cobble.bot.common.ref.BitTrackingRef

trait SingleCheerBitGameMode extends BitGameMode {

    val defaultGoalAmount: Int

    val defaultGoalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT

    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.getGoalAmountLocation(domain)).getOrElse(defaultGoalAmount)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.getGoalAmountLocation(domain), goalAmount)

    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    def getGoalCount: Int = cache.get[Int](BitTrackingRef.getGoalCountLocation(domain)).getOrElse(defaultGoalCount)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.getGoalCountLocation(domain), goalCount)

    override def getFormattingVariables: Map[String, String] = Map(
        "goalAmount" -> numberFormatString.format(getGoalAmount),
        "goalCount" -> numberFormatString.format(getGoalCount)
    )

}
