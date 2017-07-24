package com.cobble.bot.common.api

import com.cobble.bot.common.models.FilterSettings

trait Filter {

    def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean

}
