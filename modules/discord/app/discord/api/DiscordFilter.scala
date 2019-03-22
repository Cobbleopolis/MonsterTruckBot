package discord.api

import common.models.FilterSettings
import discord4j.core.`object`.entity.Message

trait DiscordFilter {

    def doesMessageMatchFilter(str: String, settings: FilterSettings): Boolean

    def filterMessage(message: Message, filterSettings: FilterSettings): Boolean = {
        val matchFilter: Boolean = doesMessageMatchFilter(message.getContent.orElse(""), filterSettings)
        if(matchFilter)
            onMessageFilter(message)
        matchFilter
    }

    def onMessageFilter(message: Message): Unit

}
