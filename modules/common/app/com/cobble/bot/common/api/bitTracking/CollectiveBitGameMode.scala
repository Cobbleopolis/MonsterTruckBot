package com.cobble.bot.common.api.bitTracking

import com.cobble.bot.common.models.bitTrackingFormData.CollectiveModeFormData
import com.cobble.bot.common.ref.BitTrackingRef

import scala.collection.mutable

trait CollectiveBitGameMode extends BitGameMode {

    val defaultGoalAmount: Int

    val defaultToNextGoalAmount: Int = 0

    val defaultGoalCount: Int = 0

    def addToToNextGoalAmount(delta: Int): Unit = setToNextGoal(getToNextGoal + delta)

    def getToNextGoal: Int = cache.get[Int](BitTrackingRef.getToNextGoalLocation(domain)).getOrElse(defaultToNextGoalAmount)

    def setToNextGoal(toNextGoalAmount: Int): Unit = cache.set(BitTrackingRef.getToNextGoalLocation(domain), toNextGoalAmount)

    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    def getGoalCount: Int = cache.get[Int](BitTrackingRef.getGoalCountLocation(domain)).getOrElse(defaultGoalCount)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.getGoalCountLocation(domain), goalCount)

    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        "goalAmount" -> numberFormatString.format(getGoalAmount),
        "toNextGoal" -> numberFormatString.format(getToNextGoal),
        "goalCount" -> numberFormatString.format(getGoalCount)
    )

    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.getGoalAmountLocation(domain)).getOrElse(defaultGoalAmount)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.getGoalAmountLocation(domain), goalAmount)

    def getCollectiveModeFormData(template: String): CollectiveModeFormData = CollectiveModeFormData(template, getGoalAmount, getToNextGoal, getGoalCount)

    def setFromCollectiveModeFormData(collectiveModeFormData: CollectiveModeFormData): Unit = {
        setGoalAmount(collectiveModeFormData.goalAmount)
        setToNextGoal(collectiveModeFormData.toNextGoal)
        setGoalCount(collectiveModeFormData.goalCount)
    }

}
