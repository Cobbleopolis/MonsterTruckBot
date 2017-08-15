package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.BitTrackingMode
import com.cobble.bot.common.api.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.models.bitTrackingFormData.RBGModeFormData
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

class RBGMode @Inject()(val cache: SyncCacheApi) extends SingleCheerBitGameMode {

    override val defaultGoalAmount: Int = BitTrackingRef.BitCheerLevels.RED_BIT

    override val mode: BitTrackingMode = BitTrackingMode.RBG

    // Green shot
    private val GREEN_SHOT_COUNT_SUFFIX: String = "greenShotCount"
    private val GREEN_SHOT_AMOUNT_SUFFIX: String = "greenShotAmount"

    // Blue shot
    private val BLUE_SHOT_COUNT_SUFFIX: String = "blueShotCount"
    private val BLUE_SHOT_AMOUNT_SUFFIX: String = "blueShotAmount"

    //Red shot
    private val RED_SHOT_COUNT_SUFFIX: String = "redShotCount"
    private val RED_SHOT_AMOUNT_SUFFIX: String = "redShotAmount"

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

    override def getFormattingVariables: Map[String, String] = Map(
        "greenShotAmount" -> numberFormatString.format(getGreenShotAmount),
        "greenShotCount" -> numberFormatString.format(getGreenShotCount),
        "blueShotAmount" -> numberFormatString.format(getBlueShotAmount),
        "blueShotCount" -> numberFormatString.format(getBlueShotCount),
        "redShotAmount" -> numberFormatString.format(getRedShotAmount),
        "redShotCount" -> numberFormatString.format(getRedShotCount)
    )

    def getRBGFormData(template: String): RBGModeFormData = RBGModeFormData(template, getGreenShotAmount, getGreenShotCount, getBlueShotAmount, getBlueShotCount, getRedShotAmount, getRedShotCount)
}
