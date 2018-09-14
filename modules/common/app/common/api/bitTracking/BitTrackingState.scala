package common.api.bitTracking

import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.components.DaoComponents
import common.models.BitTrackingSettings
import common.models.bitTrackingFormData.BitTrackingFormData
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.Logger
import play.api.cache.SyncCacheApi
import play.api.db.Database

import scala.collection.mutable

class BitTrackingState @Inject()(daoComponents: DaoComponents, configRef: MtrConfigRef, cache: SyncCacheApi) {

    private val numberFormatString: String = "%,d"

    private def getBitTrackingLocation(suffix: String): String = s"bitTracking.$suffix"


    private val BIT_TRACKING_MODE_SUFFIX: String = "bitTrackingMode"

    def getCurrentBitTrackingMode: BitTrackingMode = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponents.bitTrackingSettingsDAO.get(configRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            BitTrackingMode(bitTrackingSettingsOpt.get.currentMode)
        else
            BitTrackingMode(0)
    }


    private val PAUSED_SUFFIX: String = "paused"

    private val PAUSED_LOCATION: String = getBitTrackingLocation(PAUSED_SUFFIX)

    def getIsPaused: Boolean = cache.getOrElseUpdate(PAUSED_LOCATION)(false)

    def setIsPaused(isPaused: Boolean): Unit = cache.set(PAUSED_LOCATION, isPaused)

    def getGameMessage: String = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponents.bitTrackingSettingsDAO.get(configRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            bitTrackingSettingsOpt.get.bitGameMessage
        else
            "bot.bitTracking.settingsNotFound"
    }

    def getBitsMessage: String = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponents.bitTrackingSettingsDAO.get(configRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            bitTrackingSettingsOpt.get.bitsMessage
        else
            "bot.bitTracking.settingsNotFound"
    }

    def getGoalMessage: String = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponents.bitTrackingSettingsDAO.get(configRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            bitTrackingSettingsOpt.get.goalMessage
        else
            "bot.bitTracking.settingsNotFound"
    }


    private val TO_NEXT_GOAL_SUFFIX: String = "toNextGoal"

    private val TO_NEXT_GOAL_LOCATION: String = getBitTrackingLocation(TO_NEXT_GOAL_SUFFIX)

    def getToNextGoal: Int = cache.getOrElseUpdate(TO_NEXT_GOAL_LOCATION)(0)

    def setToNextGoal(toNextGoal: Int): Unit = cache.set(TO_NEXT_GOAL_LOCATION, toNextGoal)

    def addToNextGoal(delta: Int): Unit = setToNextGoal(getToNextGoal + delta)


    private val GOAL_AMOUNT_SUFFIX: String = "goalAmount"

    private val GOAL_AMOUNT_LOCATION: String = getBitTrackingLocation(GOAL_AMOUNT_SUFFIX)

    def getGoalAmount: Int = cache.getOrElseUpdate(GOAL_AMOUNT_LOCATION)(10000)

    def setGoalAmount(goalAmount: Int): Unit = cache.set(GOAL_AMOUNT_LOCATION, goalAmount)


    private val GOAL_COUNT_SUFFIX: String = "goalCount"

    private val GOAL_COUNT_LOCATION: String = getBitTrackingLocation(GOAL_COUNT_SUFFIX)

    def getGoalCount: Int = cache.getOrElseUpdate(GOAL_COUNT_LOCATION)(0)

    def setGoalCount(goalCount: Int): Unit = cache.set(GOAL_COUNT_LOCATION, goalCount)

    def addToGoalCount(delta: Int): Unit = setGoalCount(getGoalCount + delta)

    private val INCREMENT_AMOUNT_SUFFIX: String = "incrementAmount"

    private val INCREMENT_AMOUNT_LOCATION: String = getBitTrackingLocation(INCREMENT_AMOUNT_SUFFIX)

    def getIncrementAmount: Int = cache.getOrElseUpdate(INCREMENT_AMOUNT_LOCATION)(1)

    def setIncrementAmount(incrementAmount: Int): Unit = cache.set(INCREMENT_AMOUNT_LOCATION, incrementAmount)

    def addToGoalAmount(delta: Int): Unit = setGoalAmount(getGoalAmount + delta)

    private val GOAL_INCREMENT_AMOUNT_SUFFIX: String = "goalIncrementAmount"

    private val GOAL_INCREMENT_AMOUNT_LOCATION: String = getBitTrackingLocation(GOAL_INCREMENT_AMOUNT_SUFFIX)

    def getGoalIncrementAmount: Int = cache.getOrElseUpdate(GOAL_INCREMENT_AMOUNT_LOCATION)(0)

    def setGoalIncrementAmount(incrementAmount: Int): Unit = cache.set(GOAL_INCREMENT_AMOUNT_LOCATION, incrementAmount)


    def getBitTrackingFormData: BitTrackingFormData = BitTrackingFormData(
        configRef.guildId,
        getCurrentBitTrackingMode.id,
        getIsPaused,
        getGameMessage,
        getBitsMessage,
        getGoalMessage,
        getToNextGoal,
        getGoalAmount,
        getGoalCount,
        getIncrementAmount,
        getGoalIncrementAmount
    )

    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
        setIsPaused(bitTrackingFormData.isPaused)
        setToNextGoal(bitTrackingFormData.toNextGoal)
        setGoalAmount(bitTrackingFormData.goalAmount)
        setGoalCount(bitTrackingFormData.goalCount)
        setIncrementAmount(bitTrackingFormData.incrementAmount)
        setGoalIncrementAmount(bitTrackingFormData.goalIncrementAmount)
    }

    def getFormattingVariables: mutable.LinkedHashMap[String, Object] = mutable.LinkedHashMap(
        GOAL_AMOUNT_SUFFIX -> getGoalAmount.asInstanceOf[Object],
        TO_NEXT_GOAL_SUFFIX -> getToNextGoal.asInstanceOf[Object],
        GOAL_COUNT_SUFFIX -> getGoalCount.asInstanceOf[Object],
        INCREMENT_AMOUNT_SUFFIX -> getIncrementAmount.asInstanceOf[Object],
        GOAL_INCREMENT_AMOUNT_SUFFIX -> getGoalIncrementAmount.asInstanceOf[Object]
    )

    def getGoalMessageVariables(delta: Int = 0): mutable.LinkedHashMap[String, Object] = getFormattingVariables += ("delta" -> delta.asInstanceOf[Object])

    private def variableMapStr(map: mutable.LinkedHashMap[String, Object]): String = map.keys.mkString("{", "}, {", "}")

    def getFormattingVariablesString: String = variableMapStr(getFormattingVariables)

    def getGoalFormattingVariablesString: String = variableMapStr(getGoalMessageVariables())

}
