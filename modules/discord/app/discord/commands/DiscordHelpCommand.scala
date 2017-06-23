package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.HelpCommand
import com.cobble.bot.common.ref.MtrConfigRef
import com.google.inject.Provider
import discord.DiscordCommandRegistry
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.util.DiscordMessageUtil
import play.api.i18n.MessagesApi

class DiscordHelpCommand @Inject()(val messageUtil: DiscordMessageUtil,
                                   messagesApi: MessagesApi,
                                   config: MtrConfigRef,
                                   commandRegistry: Provider[DiscordCommandRegistry]
                                  ) extends DiscordCommand with HelpCommand {

    override def execute(implicit event: CommandExecutionEvent): Unit = {
        var messageStr: String = ""
        if (event.getArgs.isEmpty) {
            messageStr += messagesApi("bot.help.general.prefix")
            commandRegistry.get().commands.keys.foreach(msg =>
                messageStr += messagesApi("bot.help.general.commandFormat", config.commandPrefix + msg, messagesApi(s"bot.help.descriptions.$msg"))
            )
        }
        messageUtil.replyDM(messageStr)
    }

}
