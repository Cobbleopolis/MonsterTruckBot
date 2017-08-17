package com.cobble.bot.common.api.bitTracking

import com.cobble.bot.common.models.bitTrackingFormData.CollectiveModeFormData
import com.cobble.bot.common.ref.BitTrackingRef

import scala.collection.mutable

trait CollectiveBitGameMode extends BitGameMode {

    val defaultGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT

    val defaultToNextGoalAmount: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL

    val defaultGoalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT

    // To Next Amount
    def addToToNextGoalAmount(delta: Int): Unit = setToNextGoal(getToNextGoal + delta)

    def getToNextGoal: Int = cache.get[Int](BitTrackingRef.getToNextGoalLocation(domain)).getOrElse(defaultToNextGoalAmount)

    def setToNextGoal(toNextGoalAmount: Int): Unit = cache.set(BitTrackingRef.getToNextGoalLocation(domain), toNextGoalAmount)

    // Goal count
    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    def getGoalCount: Int = cache.get[Int](BitTrackingRef.getGoalCountLocation(domain)).getOrElse(defaultGoalCount)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.getGoalCountLocation(domain), goalCount)

    // Goal amount
    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.getGoalAmountLocation(domain)).getOrElse(defaultGoalAmount)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.getGoalAmountLocation(domain), goalAmount)


    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        BitTrackingRef.GOAL_AMOUNT_SUFFIX -> numberFormatString.format(getGoalAmount),
        BitTrackingRef.TO_NEXT_GOAL_SUFFIX -> numberFormatString.format(getToNextGoal),
        BitTrackingRef.GOAL_COUNT_SUFFIX -> numberFormatString.format(getGoalCount)
    )

    // Form data
    def getCollectiveModeFormData(template: String): CollectiveModeFormData = CollectiveModeFormData(template, getGoalAmount, getToNextGoal, getGoalCount)

    def setFromCollectiveModeFormData(collectiveModeFormData: CollectiveModeFormData): Unit = {
        setGoalAmount(collectiveModeFormData.goalAmount)
        setToNextGoal(collectiveModeFormData.toNextGoal)
        setGoalCount(collectiveModeFormData.goalCount)
    }

}
