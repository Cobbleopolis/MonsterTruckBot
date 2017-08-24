package twitch.util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.ref.MessageRef
import com.cobble.bot.common.util.MessageUtil
import org.kitteh.irc.client.library.element.Channel
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import play.api.i18n.MessagesApi
import twitch.TwitchBot
import twitch.api.TwitchChatMessageEvent

@Singleton
class TwitchMessageUtil @Inject()(implicit val messagesApi: MessagesApi, twitchBot: TwitchBot) extends MessageUtil(messagesApi) {

    def reply(content: String, args: Any*)(implicit event: TwitchChatMessageEvent): Unit = replyToMessage(event.getMessageEvent, event.displayName, content, args: _*)

    def replyToMessage(message: ChannelMessageEvent, displayName: String, content: String, args: Any*): Unit = {
        val userMention: String = if (displayName.startsWith("@")) displayName else "@" + displayName
        sendMessage(message, cleanMessage(formatMessage(userMention, content, args: _*)))
    }

    def replyMe(content: String, args: Any*)(implicit event: TwitchChatMessageEvent): Unit = replyToMessageWithMe(event.getMessageEvent, content, args: _*)

    def replyToMessageWithMe(message: ChannelMessageEvent, content: String, args: Any*): Unit = sendMessage(message, s"/me ${messagesApi(content, args: _*)}")

    def sendMessageToChannel(channel: Channel, content: String, args: Any*): Unit = twitchBot.client.sendMessage(channel, s"/me ${localizeMessage(content, args: _*)}")

    def replyToChannel(channel: Channel, displayName: String, content: String, args: Any*): Unit = {
        val userMention: String = if (displayName.startsWith("@")) displayName else "@" + displayName
        twitchBot.client.sendMessage(channel, cleanMessage(formatMessage(userMention, content, args: _*)))
    }

    def sendMessage(message: ChannelMessageEvent, localizedContent: String): Unit = {
        if (localizedContent.length > MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE)
            message.sendReply(cleanMessage(messagesApi("bot.commandMessageTooLong")).trim)
        else
            message.sendReply(cleanMessage(localizedContent))
    }

    def localizeMessage(content: String, args: Any*): String = {
        val localizedMsg: String = messagesApi(content, args: _*)
        if (localizedMsg.length > MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE)
            cleanMessage(messagesApi("bot.commandMessageTooLong")).trim
        else
            cleanMessage(localizedMsg).trim
    }

    def cleanMessage(message: String): String = message.replaceAll("\\R", " | ")

}
