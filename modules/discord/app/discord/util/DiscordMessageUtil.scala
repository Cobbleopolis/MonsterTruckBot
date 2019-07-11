package discord.util

import ackcord.{CacheSnapshot, Id}
import ackcord.data.{Channel, Message, User}
import ackcord.requests.{GetChannel, GetUser}
import javax.inject.{Inject, Singleton}
import common.ref.MessageRef
import common.util.MessageUtil
import discord.event.DiscordCommandExecutionEvent
import play.api.i18n.MessagesApi
import ackcord.syntax._

@Singleton
class DiscordMessageUtil @Inject()(implicit val messagesApi: MessagesApi) extends MessageUtil(messagesApi) {

    override val maxMessageLength: Int = MessageRef.DISCORD_MAX_MESSAGE_LENGTH

    def reply(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessage(event.getMessage, content, args: _*)

    def replyDM(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = sendDM(event.getUser, content, args: _*)

    def sendDM(user: User, content: String, args: Any*): Unit = ???

    def replyToMessage(message: Message, content: String, args: Any*): Unit = sendMessage(message, formatMessage(Some(""), content, args: _*))

    def replyNoAt(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessageWithoutAt(event.getMessage, content, args: _*)

    def replyToMessageWithoutAt(message: Message, content: String, args: Any*): Unit = sendMessage(message, formatMessage(None, content, args: _*))

    def sendMessage(message: Message, localizedContent: String): Unit = ???

    def sendMessage(channel: Channel, localizedContent: String): Unit = channel.asTChannel.map(_.sendMessage(localizedContent))

}
