package com.cobble.bot.common.api.filters

import com.cobble.bot.common.api.Filter
import com.cobble.bot.common.models.FilterSettings

trait CapsFilter extends Filter {

    override def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean = s"[\\p{javaUpperCase}\\s]{${filterSettings.capsFilterThreshold}}".r.findFirstIn(messageContent).isDefined

}
