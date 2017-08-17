package com.cobble.bot.common.api.bitTracking

import com.cobble.bot.common.ref.BitTrackingRef

import scala.collection.mutable

trait SingleCheerBitGameMode extends BitGameMode {

    val defaultGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT

    val defaultGoalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT

    // Goal amount
    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.getGoalAmountLocation(domain)).getOrElse(defaultGoalAmount)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.getGoalAmountLocation(domain), goalAmount)

    // Goal count
    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    def getGoalCount: Int = cache.get[Int](BitTrackingRef.getGoalCountLocation(domain)).getOrElse(defaultGoalCount)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.getGoalCountLocation(domain), goalCount)

    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        BitTrackingRef.GOAL_AMOUNT_SUFFIX -> numberFormatString.format(getGoalAmount),
        BitTrackingRef.GOAL_COUNT_SUFFIX -> numberFormatString.format(getGoalCount)
    )

}
