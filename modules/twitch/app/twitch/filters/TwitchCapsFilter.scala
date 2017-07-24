package twitch.filters

import javax.inject.Inject

import com.cobble.bot.common.api.filters.CapsFilter
import com.cobble.bot.common.models.FilterSettings
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchCapsFilter @Inject()(twitchMessageUtil: TwitchMessageUtil) extends CapsFilter {

    def filterMessage(message: TwitchMessageEvent, filterSettings: FilterSettings): Unit = {
        if(doesMessageMatchFilter(message.getMessage, filterSettings)) {
            message.timeoutUser("stop spamming caps")
            twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.noCaps")
        }
    }
}
