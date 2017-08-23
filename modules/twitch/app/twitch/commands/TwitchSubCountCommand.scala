package twitch.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.SubCountCommand
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.TwitchApiUtil
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class TwitchSubCountCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends TwitchCommand with SubCountCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        var channelName: String = executionEvent.channelName
        if (executionEvent.getArgs.headOption.isDefined && mtrConfigRef.twitchChannels.contains(executionEvent.getArgs.head.toLowerCase))
            channelName = executionEvent.getArgs.head.toLowerCase

        getTotalSubCount(mtrConfigRef.twitchChannels.get(channelName), twitchApiUtil)(executionContext, messageUtil.messagesApi).onComplete {
            case Success(subCount) =>
                if (messageUtil.isDefined("bot.count." + channelName))
                    messageUtil.replyMe("bot.count." + channelName, subCount)
                else
                    messageUtil.replyMe("bot.count.default", subCount.toString)
            case Failure(t) =>
                messageUtil.reply("bot.count.error", t.getMessage)
                twitch.TwitchLogger.error("Error getting twitch sub count", t)
        }
    }

}
