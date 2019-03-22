package discord.commands

import common.api.TwitchChannelInfo
import common.api.commands.UptimeCommand
import common.ref.MtrConfigRef
import common.util.TwitchApiUtil
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject
import reactor.core.scala.publisher.Mono

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DiscordUptimeCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends DiscordCommand with UptimeCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Mono[Message] = {
        var twitchChannel: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.values.headOption
        if (event.getArgs.headOption.isDefined && mtrConfigRef.twitchChannels.contains(event.getArgs.head.toLowerCase()))
            twitchChannel = mtrConfigRef.twitchChannels.get(event.getArgs.head.toLowerCase())

        messageUtil.replyNoAt("Working on that one...")

//        getStreamUptime(twitchChannel, twitchApiUtil)(executionContext, messageUtil.messagesApi).onComplete {
//            case Success(uptime) =>
//                messageUtil.replyNoAt(uptime)
//            case Failure(t) =>
//                messageUtil.reply("bot.commandExecutionError", t.getMessage)
//                discord.DiscordLogger.error("Error getting twitch uptime", t)
//        }
    }

}
