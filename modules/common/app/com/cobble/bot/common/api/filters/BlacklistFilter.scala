package com.cobble.bot.common.api.filters

import com.cobble.bot.common.api.Filter
import com.cobble.bot.common.models.FilterSettings

trait BlacklistFilter extends Filter {

    override def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean = filterSettings.blacklistFilterWords.split("\\r\\n").exists(word => word.r.findFirstIn(messageContent).isDefined)

}
