package discord.api

import ackcord.data.Message
import common.models.FilterSettings

trait DiscordFilter {

    def doesMessageMatchFilter(str: String, settings: FilterSettings): Boolean

    def filterMessage(message: Message, filterSettings: FilterSettings): Boolean = {
        val matchFilter: Boolean = doesMessageMatchFilter(message.content, filterSettings)
        if(matchFilter)
            onMessageFilter(message)
        matchFilter
    }

    def onMessageFilter(message: Message): Unit

}
