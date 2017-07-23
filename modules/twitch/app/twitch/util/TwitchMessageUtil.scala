package twitch.util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.util.MessageUtil
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import play.api.i18n.MessagesApi
import twitch.events.TwitchCommandExecutionEvent

@Singleton
class TwitchMessageUtil @Inject()(implicit messagesApi: MessagesApi) extends DefaultLang {

    def reply(content: String, args: Any*)(implicit event: TwitchCommandExecutionEvent): Unit = replyToMessage(event.getMessageEvent, event.displayName, content, args)

    def replyToMessage(message: ChannelMessageEvent, displayName: String, content: String, args: Any*): Unit = message.sendReply(MessageUtil.formatMessage("@" + displayName, content, args))
}
