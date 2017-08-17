package twitch.filters

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.filters.CapsFilter
import com.cobble.bot.common.models.FilterSettings
import play.api.i18n.MessagesApi
import twitch.api.TwitchFilter
import twitch.events.TwitchMessageEvent
import twitch.util.TwitchMessageUtil

class TwitchCapsFilter @Inject()(twitchMessageUtil: TwitchMessageUtil, messagesApi: MessagesApi) extends TwitchFilter with CapsFilter with DefaultLang {

    override def onMessageFilter(message: TwitchMessageEvent): Unit = {
        message.timeoutUser(messagesApi("bot.filter.noCaps.reason"))
        twitchMessageUtil.replyToMessage(message.getMessageEvent, message.displayName, "bot.filter.noCaps.message")
    }
}
