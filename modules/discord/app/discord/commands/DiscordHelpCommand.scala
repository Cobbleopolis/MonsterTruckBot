package discord.commands

import common.DefaultLang
import common.api.commands.HelpCommand
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject
import reactor.core.scala.publisher.Mono

class DiscordHelpCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, config: MtrConfigRef) extends DiscordCommand with HelpCommand with DefaultLang {

    override def execute(implicit event: DiscordCommandExecutionEvent): Mono[Message] = messageUtil.reply("bot.help.message", config.helpLink)

}
