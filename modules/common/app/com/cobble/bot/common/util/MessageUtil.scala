package com.cobble.bot.common.util

import com.cobble.bot.common.DefaultLang
import play.api.i18n.MessagesApi

abstract class MessageUtil(messagesApi: MessagesApi) extends DefaultLang {

    /**
      * The arrow character used when responding to a command.
      */
    val arrowChar: Char = '\u21D2'

    /**
      * Properly formats a response message mentioning a user.
      *
      * @param userMention A string used to mention a user.
      * @param message     The content of the message to format.
      * @return The formatted message.
      */
    def formatMessage(userMention: String, message: String, args: Any*): String = {
        if (messagesApi.isDefinedAt(message))
            s"$userMention $arrowChar ${messagesApi(message, args: _*)}"
        else
            s"$userMention $arrowChar $message"
    }

    def isDefined(key: String): Boolean = messagesApi.isDefinedAt(key)
}
