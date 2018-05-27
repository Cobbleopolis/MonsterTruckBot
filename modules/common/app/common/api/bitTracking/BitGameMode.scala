package common.api.bitTracking

import BitTrackingGame.BitTrackingGame
import play.api.cache.SyncCacheApi

import scala.collection.mutable

abstract class BitGameMode {

    lazy val domain: String = mode.toString.toLowerCase()

    val cache: SyncCacheApi

    val mode: BitTrackingGame

    val numberFormatString: String = "%,d"

    def getFormattingVariables: mutable.LinkedHashMap[String, String]

    def getFormattingVariablesString: String = getFormattingVariables.keys.mkString("{", "}, {", "}")
}
