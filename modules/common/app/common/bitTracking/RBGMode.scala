package common.bitTracking

import javax.inject.Inject

import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.api.bitTracking.{BitTrackingMode, SingleCheerBitGameMode}
import common.models.bitTrackingFormData.RBGModeFormData
import common.ref.BitTrackingRef
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

    def addToGreenShotCount(delta: Int): Unit = setGreenShotCount(getGreenShotCount + delta)

    def getGreenShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    def addToBlueShotCount(delta: Int): Unit = setBlueShotCount(getBlueShotCount + delta)

    def getBlueShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    def addToRedShotCount(delta: Int): Unit = setRedShotCount(getRedShotCount + delta)

    def getRedShotCount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_COUNT_SUFFIX)).getOrElse(defaultGoalCount)

    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        GREEN_SHOT_COUNT_SUFFIX -> numberFormatString.format(getGreenShotAmount),
        GREEN_SHOT_COUNT_SUFFIX -> numberFormatString.format(getGreenShotCount),
        BLUE_SHOT_AMOUNT_SUFFIX -> numberFormatString.format(getBlueShotAmount),
        BLUE_SHOT_COUNT_SUFFIX -> numberFormatString.format(getBlueShotCount),
        RED_SHOT_AMOUNT_SUFFIX -> numberFormatString.format(getRedShotAmount),
        RED_SHOT_COUNT_SUFFIX -> numberFormatString.format(getRedShotCount)
    )

    // Green shot
    def getGreenShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.GREEN_BIT)

    // Blue shot
    def getBlueShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.BLUE_BIT)

    // Red shot
    def getRedShotAmount: Int = cache.get[Int](BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_AMOUNT_SUFFIX)).getOrElse(BitTrackingRef.BitCheerLevels.RED_BIT)

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

    def setGreenShotAmount(greenShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_AMOUNT_SUFFIX), greenShotAmount)

    def setGreenShotCount(greenShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, GREEN_SHOT_COUNT_SUFFIX), greenShotCount)

    def setBlueShotAmount(blueShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_AMOUNT_SUFFIX), blueShotAmount)

    def setBlueShotCount(blueShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, BLUE_SHOT_COUNT_SUFFIX), blueShotCount)

    def setRedShotAmount(redShotAmount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_AMOUNT_SUFFIX), redShotAmount)

    def setRedShotCount(redShotCount: Int): Unit = cache.set(BitTrackingRef.getBitTrackingLocation(domain, RED_SHOT_COUNT_SUFFIX), redShotCount)
}
