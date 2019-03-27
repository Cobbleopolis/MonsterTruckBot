package discord.commands

import common.api.TwitchChannelInfo
import common.api.commands.SubCountCommand
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

class DiscordSubCountCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends DiscordCommand with SubCountCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Mono[Message] = {
        var twitchChannel: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.values.headOption
        if (event.getArgs.headOption.isDefined && mtrConfigRef.twitchChannels.contains(event.getArgs.head.toLowerCase()))
            twitchChannel = mtrConfigRef.twitchChannels.get(event.getArgs.head.toLowerCase())

        Mono.fromFuture(getTotalSubCount(twitchChannel, twitchApiUtil)(executionContext, messageUtil.messagesApi).transformWith {
            case Success(subCount) =>
                val channelName: String = twitchChannel.get.name.toLowerCase
                if (messageUtil.isDefined(s"bot.count.$channelName"))
                    messageUtil.replyNoAt(s"bot.count.$channelName", subCount).toFuture
                else
                    messageUtil.replyNoAt(s"bot.count.default", subCount).toFuture
            case Failure(t) =>
                discord.DiscordLogger.error("Error getting twitch sub count", t)
                messageUtil.reply("bot.count.error", t.getMessage).toFuture
        })
    }

}
