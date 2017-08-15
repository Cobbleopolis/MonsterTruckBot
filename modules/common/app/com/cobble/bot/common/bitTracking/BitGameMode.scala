package com.cobble.bot.common.bitTracking

import com.cobble.bot.common.api.BitTrackingMode.BitTrackingMode
import play.api.cache.SyncCacheApi

abstract class BitGameMode {

    lazy val domain: String = mode.toString.toLowerCase()

    val cache: SyncCacheApi

    val mode: BitTrackingMode

    val numberFormatString: String = "%,d"

    def getFormattingVariables: Map[String, String]

    def getFormattingVariablesString: String = getFormattingVariables.keys.mkString("{", "}, {", "}")
}
