package com.cobble.bot.discord.util

import com.cobble.bot.common.util.MessageUtil
import sx.blah.discord.handle.obj.IMessage

object DiscordMessageUtil {

    def reply(content: String)(implicit message: IMessage): Unit = reply(message, content)

    def reply(message: IMessage, content: String): Unit = message.getChannel.sendMessage(MessageUtil.formatMessage(message.getAuthor.mention(), content))

}
