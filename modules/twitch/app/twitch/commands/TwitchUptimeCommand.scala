package twitch.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.UptimeCommand
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.TwitchApiUtil
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class TwitchUptimeCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends TwitchCommand with UptimeCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        var channelName: String = executionEvent.channelName
        if (executionEvent.getArgs.headOption.isDefined && mtrConfigRef.twitchChannels.contains(executionEvent.getArgs.head.toLowerCase))
            channelName = executionEvent.getArgs.head.toLowerCase

        getStreamUptime(mtrConfigRef.twitchChannels.get(channelName), twitchApiUtil)(executionContext, messageUtil.messagesApi).onComplete {
            case Success(uptime) =>
                messageUtil.reply(uptime)
            case Failure(t) =>
                messageUtil.reply("bot.commandExecutionError", t.getMessage)
                twitch.TwitchLogger.error("Error getting twitch uptime", t)
        }
    }
}
