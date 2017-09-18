package twitch.util

import javax.inject.{Inject, Provider, Singleton}

import com.cobble.bot.common.ref.MessageRef
import com.cobble.bot.common.util.MessageUtil
import org.kitteh.irc.client.library.element.Channel
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import play.api.i18n.MessagesApi
import twitch.TwitchBot
import twitch.api.TwitchChatMessageEvent

@Singleton
class TwitchMessageUtil @Inject()(implicit val messagesApi: MessagesApi, twitchBot: Provider[TwitchBot]) extends MessageUtil(messagesApi) {

    override val maxMessageLength: Int = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE

    def reply(content: String, args: Any*)(implicit event: TwitchChatMessageEvent): Unit = replyToMessage(event.getMessageEvent, event.displayName, content, args: _*)

    def replyToMessage(message: ChannelMessageEvent, displayName: String, content: String, args: Any*): Unit = {
        val userMention: String = if (displayName.startsWith("@")) displayName else "@" + displayName
        message.sendReply(formatMessage(Some(userMention), content, args: _*))
    }

    def sendMessageToChannel(channel: Channel, content: String, args: Any*): Unit = twitchBot.get().client.sendMessage(channel, formatMessage(None, content, args: _*))

    def replyToChannel(channel: Channel, displayName: String, content: String, args: Any*): Unit = {
        val userMention: String = if (displayName.startsWith("@")) displayName else "@" + displayName
        twitchBot.get().client.sendMessage(channel, formatMessage(Some(userMention), content, args: _*))
    }

    override def formatMessageText(userMention: String, message: String): String = "/me " + super.formatMessageText(userMention, message)

    override def cleanMessage(localizedMessage: String): String = super.cleanMessage(localizedMessage).replaceAll("\\R", " | ").trim

}
