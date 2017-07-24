package twitch.filters

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.filters.LinksFilter
import com.cobble.bot.common.models.FilterSettings
import play.api.i18n.MessagesApi
import twitch.api.TwitchFilter
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchLinksFilter @Inject()(twitchMessageUtil: TwitchMessageUtil, messagesApi: MessagesApi) extends TwitchFilter with LinksFilter with DefaultLang {

    override def filterMessage(message: TwitchMessageEvent, filterSettings: FilterSettings): Unit = {
        if (doesMessageMatchFilter(message.getMessage, filterSettings)) {
            message.timeoutUser(messagesApi("bot.filter.noLinks.reason"))
            twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.noLinks.message")
        }
    }

}
