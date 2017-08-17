package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.bitTracking.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.api.bitTracking.{BitTrackingMode, SingleCheerBitGameMode}
import com.cobble.bot.common.models.bitTrackingFormData.RBGModeFormData
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

import scala.collection.mutable

class RBGMode @Inject()(val cache: SyncCacheApi) extends SingleCheerBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.RBG

    // Green shot
    private val GREEN_SHOT_AMOUNT_SUFFIX: String = "greenShotAmount"
    private val GREEN_SHOT_COUNT_SUFFIX: String = "greenShotCount"

    // Blue shot
    private val BLUE_SHOT_AMOUNT_SUFFIX: String = "blueShotAmount"
    private val BLUE_SHOT_COUNT_SUFFIX: String = "blueShotCount"

    //Red shot
    private val RED_SHOT_AMOUNT_SUFFIX: String = "redShotAmount"
    private val RED_SHOT_COUNT_SUFFIX: String = "redShotCount"

    // Green shot
    def getGreenShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.GREEN_BIT)

    def setGreenShotAmount(greenShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_AMOUNT_SUFFIX), greenShotAmount)

    def addToGreenShotCount(delta: Int): Unit = setGreenShotCount(getGreenShotCount + delta)

    def getGreenShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    def setGreenShotCount(greenShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_COUNT_SUFFIX), greenShotCount)

    // Blue shot
    def getBlueShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.BLUE_BIT)

    def setBlueShotAmount(blueShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_AMOUNT_SUFFIX), blueShotAmount)

    def addToBlueShotCount(delta: Int): Unit = setBlueShotCount(getBlueShotCount + delta)

    def getBlueShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    def setBlueShotCount(blueShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_COUNT_SUFFIX), blueShotCount)

    // Red shot
    def getRedShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.RED_BIT)

    def setRedShotAmount(redShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_AMOUNT_SUFFIX), redShotAmount)

    def addToRedShotCount(delta: Int): Unit = setRedShotCount(getRedShotCount + delta)

    def getRedShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    def setRedShotCount(redShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_COUNT_SUFFIX), redShotCount)


    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        GREEN_SHOT_COUNT_SUFFIX -> numberFormatString.format(getGreenShotAmount),
        GREEN_SHOT_COUNT_SUFFIX -> numberFormatString.format(getGreenShotCount),
        BLUE_SHOT_AMOUNT_SUFFIX -> numberFormatString.format(getBlueShotAmount),
        BLUE_SHOT_COUNT_SUFFIX -> numberFormatString.format(getBlueShotCount),
        RED_SHOT_AMOUNT_SUFFIX -> numberFormatString.format(getRedShotAmount),
        RED_SHOT_COUNT_SUFFIX -> numberFormatString.format(getRedShotCount)
    )

    // Form data
    def getRBGFormData(template: String): RBGModeFormData = RBGModeFormData(template, getGreenShotAmount, getGreenShotCount, getBlueShotAmount, getBlueShotCount, getRedShotAmount, getRedShotCount)

    def setFromRBGFormData(rbgModeFormData: RBGModeFormData): Unit = {
        setGreenShotAmount(rbgModeFormData.greenShotAmount)
        setGreenShotCount(rbgModeFormData.greenShotCount)
        setBlueShotAmount(rbgModeFormData.blueShotAmount)
        setBlueShotCount(rbgModeFormData.blueShotCount)
        setRedShotAmount(rbgModeFormData.redShotAmount)
        setRedShotCount(rbgModeFormData.redShotCount)
    }
}