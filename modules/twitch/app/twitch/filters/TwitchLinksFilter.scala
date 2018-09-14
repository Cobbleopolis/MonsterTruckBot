package twitch.filters

import common.DefaultLang
import common.api.filters.LinksFilter
import javax.inject.Inject
import play.api.i18n.MessagesApi
import twitch.api.TwitchFilter
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchLinksFilter @Inject()(twitchMessageUtil: TwitchMessageUtil, messagesApi: MessagesApi) extends TwitchFilter with LinksFilter with DefaultLang {

    override def onMessageFilter(message: TwitchMessageEvent): Unit = {
        message.timeoutUser(messagesApi("bot.filter.noLinks.reason"))
        twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.noLinks.message")
    }
}
