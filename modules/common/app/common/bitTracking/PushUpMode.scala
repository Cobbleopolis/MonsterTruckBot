package common.bitTracking

import javax.inject.Inject

import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.api.bitTracking.GameCheerMode.GameCheerMode
import common.api.bitTracking._
import common.models.bitTrackingFormData.PushUpModeFormData
import common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

import scala.collection.mutable

class PushUpMode @Inject()(val cache: SyncCacheApi) extends CollectiveBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.PUSH_UP

    // Cheer mode
    private val CHEER_MODE_SUFFIX: String = "cheerMode"

    private val CHEER_MODE_LOCATION: String = BitTrackingRef.getBitTrackingLocation(domain, CHEER_MODE_SUFFIX)

    // Push Up amount
    private val PUSH_UP_SET_AMOUNT_SUFFIX: String = "pushUpSetAmount"

    private val PUSH_UP_SET_AMOUNT_LOCATION: String = BitTrackingRef.getBitTrackingLocation(domain, PUSH_UP_SET_AMOUNT_SUFFIX)

    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        BitTrackingRef.GOAL_AMOUNT_SUFFIX -> numberFormatString.format(getGoalAmount),
        BitTrackingRef.TO_NEXT_GOAL_SUFFIX -> numberFormatString.format(getToNextGoal),
        PUSH_UP_SET_AMOUNT_SUFFIX -> numberFormatString.format(getPushSetUpAmount),
        "pushUpCount" -> numberFormatString.format(getGoalCount)
    )

    // Push Up set amount
    def getPushSetUpAmount: Int = cache.get[Int](PUSH_UP_SET_AMOUNT_LOCATION).getOrElse(BitTrackingRef.PushUpMode.DEFAULT_PUSH_UP_SET_AMOUNT)

    // Form data
    def getPushUpFormData(template: String): PushUpModeFormData = PushUpModeFormData(template, getCheerMode.toString, getGoalAmount, getToNextGoal, getPushSetUpAmount, getGoalCount)

    // Cheer mode
    def getCheerMode: GameCheerMode = cache.get[GameCheerMode](CHEER_MODE_LOCATION).getOrElse(GameCheerMode.COLLECTIVE)

    def setFromPushUpFormData(pushUpModeFormData: PushUpModeFormData): Unit = {
        setCheerMode(pushUpModeFormData.getCurrentCheerMode)
        setGoalAmount(pushUpModeFormData.goalAmount)
        setToNextGoal(pushUpModeFormData.toNextGoal)
        setPushUpSetAmount(pushUpModeFormData.pushUpSetAmount)
        setGoalCount(pushUpModeFormData.goalCount)
    }

    def setCheerMode(cheerMode: GameCheerMode): Unit = cache.set(CHEER_MODE_LOCATION, cheerMode)

    def setPushUpSetAmount(pushUpCount: Int): Unit = cache.set(PUSH_UP_SET_AMOUNT_LOCATION, pushUpCount)
}
