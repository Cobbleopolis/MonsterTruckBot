package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.api.TwitchChannelInfo
import com.cobble.bot.common.api.commands.SubCountCommand
import com.cobble.bot.common.ref.{MessageRef, MtrConfigRef}
import com.cobble.bot.common.util.TwitchApiUtil
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DiscordSubCountCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends DiscordCommand with SubCountCommand {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = {
        var twitchChannel: Option[TwitchChannelInfo] = mtrConfigRef.twitchChannels.values.headOption
        if (event.getArgs.headOption.isDefined && mtrConfigRef.twitchChannels.contains(event.getArgs.head.toLowerCase()))
            twitchChannel = mtrConfigRef.twitchChannels.get(event.getArgs.head.toLowerCase())

        getTotalSubCount(twitchChannel, twitchApiUtil)(executionContext, messageUtil.messagesApi).onComplete {
            case Success(subCount) =>
                if (messageUtil.isDefined("bot.count." +  twitchChannel.get.name))
                    messageUtil.replyNoAt("bot.count." + twitchChannel.get.name, subCount)
                else
                    messageUtil.replyNoAt("bot.count.default", subCount)
            case Failure(t) =>
                messageUtil.reply("bot.count.error", t.getMessage)
                discord.DiscordLogger.error("Error getting twitch sub count", t)
        }
    }

}
