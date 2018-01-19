package twitch.api

import common.models.FilterSettings
import twitch.events.TwitchMessageEvent

trait TwitchFilter {

    def doesMessageMatchFilter(str: String, settings: FilterSettings): Boolean

    def filterMessage(message: TwitchMessageEvent, filterSettings: FilterSettings): Boolean = {
        val matchFilter: Boolean = doesMessageMatchFilter(message.getMessage, filterSettings)
        if(matchFilter)
            onMessageFilter(message)
        matchFilter
    }

    def onMessageFilter(message: TwitchMessageEvent): Unit

}
