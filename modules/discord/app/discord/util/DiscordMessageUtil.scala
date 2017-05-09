package discord.util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.util.MessageUtil
import discord.event.CommandExecutionEvent
import play.api.i18n.MessagesApi
import sx.blah.discord.handle.obj.{IMessage, IUser}

@Singleton
class DiscordMessageUtil @Inject()(implicit messagesApi: MessagesApi) {

    def reply(content: String, args: Any*)(implicit event: CommandExecutionEvent): Unit = replyToMessage(event.getMessage, content, args)

    def replyDM(content: String, args: Any*)(implicit event: CommandExecutionEvent): Unit = sendDM(event.getUser, content, args)

//    def reply(content: String, args: Any*)(implicit message: IMessage): Unit = replyToMessage(message, content, args)

    def sendDM(user: IUser, content: String, args: Any*): Unit = user.getOrCreatePMChannel().sendMessage(messagesApi(content, args: _*))

    def replyToMessage(message: IMessage, content: String, args: Any*): Unit = message.getChannel.sendMessage(MessageUtil.formatMessage(message.getAuthor.mention(), content))

}
