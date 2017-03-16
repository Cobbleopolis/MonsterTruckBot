package com.cobble.bot.common.util

object MessageUtil {

    /**
      * The arrow character used when responding to a command.
      */
    val arrowChar: Char = '\u21D2'

    /**
      * Formats an error message mentioning a user.
      *
      * @param userMention A string used to mention a user.
      * @param message     The content of the message specific to what happened.
      * @param throwable   The error that was thrown.
      * @return The formatted error message.
      */
    def getErrorMessage(userMention: String, message: String, throwable: Throwable): String =
        formatMessage(userMention, s"$message \n\nPlease report this message to the bot developer as well as the command you used that returned this error.\n```${throwable.getMessage}```")

    /**
      * Properly formats a response message mentioning a user.
      *
      * @param userMention A string used to mention a user.
      * @param message     The content of the message to format.
      * @return The formatted message.
      */
    def formatMessage(userMention: String, message: String): String = s"$userMention $arrowChar $message"

    /**
      * Formats a message in response to a user that incorrectly used a command.
      *
      * @param userMention A string used to mention a user.
      * @param message     Text describing how the command was misused.
      * @param name        The name of the misused command.
      * @return The formatted usage error message.
      */
    def commandUsageError(userMention: String, message: String, name: String): String = formatMessage(userMention, s"$message Please use `!mthelp $name` to see how to use this command.")
}
