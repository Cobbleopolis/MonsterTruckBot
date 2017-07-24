package twitch.filters

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.filters.BlacklistFilter
import com.cobble.bot.common.models.FilterSettings
import play.api.i18n.MessagesApi
import twitch.api.TwitchFilter
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchBlacklistFilter @Inject()(twitchMessageUtil: TwitchMessageUtil, messagesApi: MessagesApi) extends TwitchFilter with BlacklistFilter with DefaultLang {

    override def filterMessage(message: TwitchMessageEvent, filterSettings: FilterSettings): Unit = {
        if(doesMessageMatchFilter(message.getMessage, filterSettings)) {
            message.timeoutUser(messagesApi("bot.filter.blacklistedWord.reason"))
            twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.blacklistedWord.message")
        }
    }

}
