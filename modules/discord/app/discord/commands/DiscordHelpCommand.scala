package discord.commands

import common.DefaultLang
import common.api.commands.HelpCommand
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject

class DiscordHelpCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, config: MtrConfigRef) extends DiscordCommand with HelpCommand with DefaultLang {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = messageUtil.reply("bot.help.message", config.helpLink)

}
