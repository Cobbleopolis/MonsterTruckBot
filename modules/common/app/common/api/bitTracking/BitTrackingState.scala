package common.api.bitTracking

import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.models.bitTrackingFormData.BitTrackingFormData
import javax.inject.Inject
import play.api.cache.SyncCacheApi

import scala.collection.mutable

class BitTrackingState @Inject()(cache: SyncCacheApi) {

    private val numberFormatString: String = "%,d"

    private def getBitTrackingLocation(suffix: String): String = s"bitTracking.$suffix"


    private val BIT_TRACKING_MODE_SUFFIX: String = "bitTrackingMode"

    private val BIT_TRACKING_MODE_LOCATION: String = getBitTrackingLocation(BIT_TRACKING_MODE_SUFFIX)

    def getCurrentBitTrackingMode: BitTrackingMode = cache.getOrElseUpdate(BIT_TRACKING_MODE_LOCATION)(BitTrackingMode.SINGLE_CHEER)

    def setCurrentBitTrackingMode(bitTrackingMode: BitTrackingMode): Unit = cache.set(BIT_TRACKING_MODE_LOCATION, bitTrackingMode)


    private val PAUSED_SUFFIX: String = "paused"

    private val PAUSED_LOCATION: String = getBitTrackingLocation(PAUSED_SUFFIX)

    def getIsPaused: Boolean = cache.getOrElseUpdate(PAUSED_LOCATION)(false)

    def setIsPaused(isPaused: Boolean): Unit = cache.set(PAUSED_LOCATION, isPaused)


    private val GAME_MESSAGE_SUFFIX: String = "gameMessage"

    private val GAME_MESSAGE_LOCATION: String = getBitTrackingLocation(GAME_MESSAGE_SUFFIX)

    def getGameMessage: String = cache.getOrElseUpdate(GAME_MESSAGE_LOCATION)("")

    def setGameMessage(gameMessage: String): Unit = cache.set(GAME_MESSAGE_LOCATION, gameMessage)


    private val BITS_MESSAGE_SUFFIX: String = "bitsMessage"

    private val BITS_MESSAGE_LOCATION: String = getBitTrackingLocation(BITS_MESSAGE_SUFFIX)

    def getBitsMessage: String = cache.getOrElseUpdate(BITS_MESSAGE_LOCATION)("")

    def setBitsMessage(bitsMessage: String): Unit = cache.set(BITS_MESSAGE_LOCATION, bitsMessage)


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


    def getBitTrackingFormData: BitTrackingFormData = BitTrackingFormData(
        getCurrentBitTrackingMode.toString,
        getIsPaused,
        getGameMessage,
        getBitsMessage,
        getToNextGoal,
        getGoalAmount,
        getGoalCount
    )

    def setBitTrackingFormData(bitTrackingFormData: BitTrackingFormData): Unit = {
        setCurrentBitTrackingMode(bitTrackingFormData.getBitTrackingMode)
        setIsPaused(bitTrackingFormData.isPaused)
        setGameMessage(bitTrackingFormData.gameMessage)
        setBitsMessage(bitTrackingFormData.bitsMessage)
        setToNextGoal(bitTrackingFormData.toNextGoal)
        setGoalAmount(bitTrackingFormData.goalAmount)
        setGoalCount(bitTrackingFormData.goalCount)
    }

    def getFormattingVariables: mutable.LinkedHashMap[String, String] = mutable.LinkedHashMap(
        GOAL_AMOUNT_SUFFIX -> numberFormatString.format(getGoalAmount),
        TO_NEXT_GOAL_SUFFIX -> numberFormatString.format(getToNextGoal),
        GOAL_COUNT_SUFFIX -> numberFormatString.format(getGoalCount)
    )

    def getFormattingVariablesString: String = getFormattingVariables.keys.mkString("{", "}, {", "}")

}
