package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.commands.HelpCommand
import com.cobble.bot.common.models.CustomCommand
import com.cobble.bot.common.ref.MtrConfigRef
import com.google.inject.Provider
import discord.DiscordCommandRegistry
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.util.DiscordMessageUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.i18n.MessagesApi

class DiscordHelpCommand @Inject()(implicit val messageUtil: DiscordMessageUtil,
                                   messagesApi: MessagesApi,
                                   config: MtrConfigRef,
                                   commandRegistry: Provider[DiscordCommandRegistry],
                                   db: Database,
                                   cacheApi: SyncCacheApi
                                  ) extends DiscordCommand with HelpCommand with DefaultLang {

    override def execute(implicit event: CommandExecutionEvent): Unit = {
        var messageStr: String = messagesApi("bot.help.general.prefix")
        commandRegistry.get().commands.keys.foreach(msg =>
            messageStr += messagesApi("bot.help.general.commandFormat", config.commandPrefix + msg, messagesApi(s"bot.help.descriptions.$msg"))
        )
        messageStr += messagesApi("bot.help.general.customCommandPrefix")
        messageStr += CustomCommand.getByGuildId(config.guildId).map(_.commandName).mkString("`", s"`, `${config.commandPrefix}", "`")
        messageUtil.replyDM(messageStr)
    }

}
