package com.cobble.bot.discord.util

import com.cobble.bot.common.util.MessageUtil
import play.api.i18n.MessagesApi
import sx.blah.discord.handle.obj.IMessage

object DiscordMessageUtil {

    def reply(content: String, args: Any*)(implicit message: IMessage, messages: MessagesApi = null): Unit = replyToMessage(message, content, args)

    def replyToMessage(message: IMessage, content: String, args: Any*)(implicit messages: MessagesApi = null): Unit = message.getChannel.sendMessage(MessageUtil.formatMessage(message.getAuthor.mention(), content))

}
