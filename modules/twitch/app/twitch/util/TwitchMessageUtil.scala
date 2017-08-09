package twitch.util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.util.MessageUtil
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import play.api.i18n.MessagesApi
import twitch.api.TwitchEvent

@Singleton
class TwitchMessageUtil @Inject()(implicit messagesApi: MessagesApi) extends DefaultLang {

    def reply(content: String, args: Any*)(implicit event: TwitchEvent): Unit = replyToMessage(event.getMessageEvent, event.displayName, content, args: _*)

    def replyToMessage(message: ChannelMessageEvent, displayName: String, content: String, args: Any*): Unit = message.sendReply(MessageUtil.formatMessage("@" + displayName, content, args: _*))

    def replyMe(content: String, args: Any*)(implicit event: TwitchEvent): Unit = replyToMessageWithMe(event.getMessageEvent, content, args: _*)

    def replyToMessageWithMe(message: ChannelMessageEvent, content: String, args: Any*): Unit = message.sendReply(s"/me ${messagesApi(content, args: _*)}")

    def isDefined(key: String): Boolean = messagesApi.isDefinedAt(key)
}
