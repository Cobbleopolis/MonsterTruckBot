package common.api.bitTracking

import BitTrackingMode.BitTrackingMode
import play.api.cache.SyncCacheApi

import scala.collection.mutable

abstract class BitGameMode {

    lazy val domain: String = mode.toString.toLowerCase()

    val cache: SyncCacheApi

    val mode: BitTrackingMode

    val numberFormatString: String = "%,d"

    def getFormattingVariables: mutable.LinkedHashMap[String, String]

    def getFormattingVariablesString: String = getFormattingVariables.keys.mkString("{", "}, {", "}")
}
