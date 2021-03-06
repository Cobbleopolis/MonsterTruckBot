package discord.util

import javax.inject.{Inject, Singleton}

import common.ref.MessageRef
import common.util.MessageUtil
import discord.event.DiscordCommandExecutionEvent
import play.api.i18n.MessagesApi
import sx.blah.discord.handle.obj.{IChannel, IMessage, IUser}

@Singleton
class DiscordMessageUtil @Inject()(implicit val messagesApi: MessagesApi) extends MessageUtil(messagesApi) {

    override val maxMessageLength: Int = MessageRef.DISCORD_MAX_MESSAGE_LENGTH

    def reply(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessage(event.getMessage, content, args: _*)

    def replyDM(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = sendDM(event.getUser, content, args: _*)

    def sendDM(user: IUser, content: String, args: Any*): Unit = sendMessage(user.getOrCreatePMChannel(), formatMessage(None, content, args: _*))

    def replyToMessage(message: IMessage, content: String, args: Any*): Unit = sendMessage(message, formatMessage(Some(message.getAuthor.mention()), content, args: _*))

    def replyNoAt(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessageWithoutAt(event.getMessage, content, args: _*)

    def replyToMessageWithoutAt(message: IMessage, content: String, args: Any*): Unit = sendMessage(message, formatMessage(None, content, args: _*))

    def sendMessage(message: IMessage, localizedContent: String): Unit = sendMessage(message.getChannel, localizedContent)

    def sendMessage(channel: IChannel, localizedContent: String): Unit = channel.sendMessage(localizedContent)

}
