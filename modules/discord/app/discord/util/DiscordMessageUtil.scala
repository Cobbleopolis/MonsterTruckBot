package discord.util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.util.MessageUtil
import discord.event.DiscordCommandExecutionEvent
import play.api.i18n.{Lang, MessagesApi}
import sx.blah.discord.handle.obj.{IMessage, IUser}

@Singleton
class DiscordMessageUtil @Inject()(implicit val messagesApi: MessagesApi) extends DefaultLang {

    def reply(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessage(event.getMessage, content, args: _*)

    def replyDM(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = sendDM(event.getUser, content, args: _*)

//    def reply(content: String, args: Any*)(implicit message: IMessage): Unit = replyToMessage(message, content, args)

    def sendDM(user: IUser, content: String, args: Any*): Unit = user.getOrCreatePMChannel().sendMessage(messagesApi(content, args: _*))

    def replyToMessage(message: IMessage, content: String, args: Any*): Unit = message.getChannel.sendMessage(MessageUtil.formatMessage(message.getAuthor.mention(), content, args))

    def replyNoAt(content: String, args: Any*)(implicit event: DiscordCommandExecutionEvent): Unit = replyToMessageWithoutAt(event.getMessage, content, args: _*)

    def replyToMessageWithoutAt(message: IMessage, content: String, args: Any*): Unit = message.getChannel.sendMessage(messagesApi(content, args: _*))

    def isDefined(key: String): Boolean = messagesApi.isDefinedAt(key)

}
