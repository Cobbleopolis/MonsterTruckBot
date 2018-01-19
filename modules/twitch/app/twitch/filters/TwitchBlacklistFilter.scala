package twitch.filters

import javax.inject.Inject

import common.DefaultLang
import common.api.filters.BlacklistFilter
import common.models.FilterSettings
import play.api.i18n.MessagesApi
import twitch.api.TwitchFilter
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchBlacklistFilter @Inject()(twitchMessageUtil: TwitchMessageUtil, messagesApi: MessagesApi) extends TwitchFilter with BlacklistFilter with DefaultLang {

    override def onMessageFilter(message: TwitchMessageEvent): Unit = {
        message.timeoutUser(messagesApi("bot.filter.blacklistedWord.reason"))
        twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.blacklistedWord.message")
    }

}
