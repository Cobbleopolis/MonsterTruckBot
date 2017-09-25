package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.commands.HelpCommand
import com.cobble.bot.common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil

class DiscordHelpCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, config: MtrConfigRef) extends DiscordCommand with HelpCommand with DefaultLang {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = messageUtil.reply("bot.help.message", config.helpLink)

}
