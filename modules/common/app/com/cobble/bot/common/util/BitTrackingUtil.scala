package com.cobble.bot.common.util

import javax.inject.Inject

import com.cobble.bot.common.models.BitTrackingFormData
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class BitTrackingUtil @Inject()(cache: SyncCacheApi) {

    def getTotalBits: Int = cache.get[Int](BitTrackingRef.TOTAL_BITS_LOCATION).getOrElse(BitTrackingRef.DEFAULT_TOTAL_BITS)

    def setTotalBits(totalBits: Int): Unit = cache.set(BitTrackingRef.TOTAL_BITS_LOCATION, totalBits)

    def addToTotalBits(delta: Int): Unit = setTotalBits(getTotalBits + delta)


    def getGoalAmount: Int = cache.get[Int](BitTrackingRef.GOAL_AMOUNT_LOCATION).getOrElse(BitTrackingRef.DEFAULT_GOAL_AMOUNT)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(BitTrackingRef.GOAL_AMOUNT_LOCATION, goalAmount)


    def getToNextGoal: Int = cache.get[Int](BitTrackingRef.TO_NEXT_GOAL_LOCATION).getOrElse(BitTrackingRef.DEFAULT_TO_NEXT_GOAL)

    def setToNextGoal(toNextGoalAmount: Int): Unit = cache.set(BitTrackingRef.TO_NEXT_GOAL_LOCATION, toNextGoalAmount)

    def addToToNextGoalAmount(delta: Int): Unit = setToNextGoal(getToNextGoal + delta)


    def getGoalCount: Int = cache.get[Int](BitTrackingRef.GOAL_COUNT_LOCATION).getOrElse(BitTrackingRef.DEFAULT_GOAL_COUNT)

    def setGoalCount(goalCount: Int): Unit = cache.set(BitTrackingRef.GOAL_COUNT_LOCATION, goalCount)

    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)


    def getBitTrackingFormData: BitTrackingFormData = BitTrackingFormData(getTotalBits, getGoalAmount, getToNextGoal, getGoalCount)

    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
        setTotalBits(bitTrackingFormData.totalBits)
        setGoalAmount(bitTrackingFormData.goalAmount)
        setToNextGoal(bitTrackingFormData.toNextGoal)
        setGoalCount(bitTrackingFormData.goalCount)
    }

}
