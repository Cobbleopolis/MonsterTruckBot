package discord.commands

import buildinfo.BuildInfo
import common.api.commands.VersionCommand
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject
import reactor.core.scala.publisher.Mono

class DiscordVersionCommand @Inject()(val messageUtil: DiscordMessageUtil) extends DiscordCommand with VersionCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Mono[Message] = messageUtil.reply(BuildInfo.version)

}
