package com.cobble.bot.common.util

import com.cobble.bot.common.DefaultLang
import play.api.i18n.MessagesApi

abstract class MessageUtil(messagesApi: MessagesApi) extends DefaultLang {

    val arrowChar: Char = '\u21D2'

    val maxMessageLength: Int

    def cleanMessage(localizedMessage: String): String = {
        if(localizedMessage.length > maxMessageLength)
            messagesApi("bot.messageTooLong").trim()
        else
            localizedMessage.trim()
    }

    def formatMessageText(userMention: Option[String], message: String): String =
        if (userMention.isDefined)
            s"${userMention.get} $arrowChar $message"
        else
            message

    def formatMessage(userMention: Option[String], message: String, args: Any*): String =
        cleanMessage(formatMessageText(userMention, messagesApi(message, args: _*)))

    def isDefined(key: String): Boolean = messagesApi.isDefinedAt(key)
}
