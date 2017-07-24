package twitch.api

import com.cobble.bot.common.models.FilterSettings
import twitch.events.TwitchMessageEvent

trait TwitchFilter {

    def filterMessage(message: TwitchMessageEvent, filterSettings: FilterSettings): Unit

}
