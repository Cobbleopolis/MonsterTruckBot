package com.cobble.bot.common.api.filters

import com.cobble.bot.common.api.Filter
import com.cobble.bot.common.models.FilterSettings

trait LinksFilter extends Filter {

    override def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)".r.findFirstIn(messageContent).isDefined

}
