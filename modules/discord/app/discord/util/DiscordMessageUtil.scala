package discord.util

import common.ref.MessageRef
import common.util.MessageUtil
import discord.event.DiscordCommandExecutionEvent
import discord4j.core.`object`.entity.{Message, MessageChannel, User}
import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import scala.compat.java8.OptionConverters._
import reactor.core.scala.publisher.Mono

@Singleton
class DiscordMessageUtil @Inject()(implicit val messagesApi: MessagesApi) extends MessageUtil(messagesApi) {

    override val maxMessageLength: Int = MessageRef.DISCORD_MAX_MESSAGE_LENGTH

    def reply(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Mono[Message] = replyToMessage(event.getMessage, content, args: _*)

    def replyDM(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Mono[Message] = sendDM(event.getUser, content, args: _*)

    def sendDM(user: User, content: String, args: Any*): Mono[Message] = sendMessage(user.getPrivateChannel.block(), formatMessage(None, content, args: _*))

    def replyToMessage(message: Message, content: String, args: Any*): Mono[Message] = sendMessage(message, formatMessage(message.getAuthor.asScala.map(_.getMention), content, args: _*))

    def replyNoAt(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Mono[Message] = replyToMessageWithoutAt(event.getMessage, content, args: _*)

    def replyToMessageWithoutAt(message: Message, content: String, args: Any*): Mono[Message] = sendMessage(message, formatMessage(None, content, args: _*))

    def sendMessage(message: Message, localizedContent: String): Mono[Message] = sendMessage(message.getChannel.block(), localizedContent)

    def sendMessage(channel: MessageChannel, localizedContent: String): Mono[Message] = Mono(channel.createMessage(localizedContent))

}
