package com.cobble.bot.common.bitTracking

import javax.inject.Inject

import com.cobble.bot.common.api.bitTracking.BitTrackingMode.BitTrackingMode
import com.cobble.bot.common.api.bitTracking.{BitTrackingMode, SingleCheerBitGameMode}
import com.cobble.bot.common.models.bitTrackingFormData.SingItOrSlamItModeFormData
import com.cobble.bot.common.ref.BitTrackingRef
import play.api.cache.SyncCacheApi

import scala.collection.mutable

class SingItOrSlamItMode @Inject()(val cache: SyncCacheApi) extends SingleCheerBitGameMode {

    override val mode: BitTrackingMode = BitTrackingMode.SING_IT_OR_SLAM_IT

    // Rounds won
    private val ROUNDS_WON_SUFFIX: String = "roundsWon"

    private val ROUNDS_WON_LOCATION: String = BitTrackingRef.getBitTrackingLocation(domain, ROUNDS_WON_SUFFIX)

    // Rounds lost
    private val ROUNDS_LOST_SUFFIX: String = "roundsLost"

    private val ROUNDS_LOST_LOACTION: String = BitTrackingRef.getBitTrackingLocation(domain, ROUNDS_LOST_SUFFIX)

    // Rounds won
    def getRoundsWon: Int = cache.get[Int](ROUNDS_WON_LOCATION).getOrElse(BitTrackingRef.SingItOrSlamIt.DEFAULT_ROUNDS_WON)

    def setRoundsWon(roundsWon: Int): Unit = cache.set(ROUNDS_WON_LOCATION, roundsWon)

    // Rounds lost
    def getRoundsLost: Int = cache.get[Int](ROUNDS_LOST_LOACTION).getOrElse(BitTrackingRef.SingItOrSlamIt.DEFAULT_ROUNDS_LOST)

    def setRoundsLost(roundsLost: Int): Unit = cache.set(ROUNDS_LOST_LOACTION, roundsLost)

    override def getFormattingVariables: mutable.LinkedHashMap[String, String] = super.getFormattingVariables ++: mutable.LinkedHashMap(
        ROUNDS_WON_SUFFIX -> numberFormatString.format(getRoundsWon),
        ROUNDS_LOST_SUFFIX -> numberFormatString.format(getRoundsLost)
    )

    // Form data
    def getSingItOrSlamItFormData(template: String): SingItOrSlamItModeFormData = SingItOrSlamItModeFormData(template, getGoalAmount, getGoalCount, getRoundsWon, getRoundsLost)

    def setFromSingItOrSlamItFormData(singItOrSlamItModeFormData: SingItOrSlamItModeFormData): Unit = {
        setGoalAmount(singItOrSlamItModeFormData.goalAmount)
        setGoalCount(singItOrSlamItModeFormData.goalCount)
        setRoundsWon(singItOrSlamItModeFormData.roundsWon)
        setRoundsLost(singItOrSlamItModeFormData.roundsLost)
    }
}
