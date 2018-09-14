package twitch.commands

import common.api.commands.SubCountCommand
import common.ref.MtrConfigRef
import common.util.TwitchApiUtil
import javax.inject.Inject
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class TwitchSubCountCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, twitchApiUtil: TwitchApiUtil, config: MtrConfigRef, executionContext: ExecutionContext) extends TwitchCommand with SubCountCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        var channelName: String = executionEvent.channelName.toLowerCase
        if (executionEvent.getArgs.headOption.isDefined && config.twitchChannels.contains(executionEvent.getArgs.head.toLowerCase))
            channelName = executionEvent.getArgs.head.toLowerCase

        getTotalSubCount(config.twitchChannels.get(channelName), twitchApiUtil)(executionContext, messageUtil.messagesApi).onComplete {
            case Success(subCount) =>
                if (messageUtil.isDefined("bot.count." + channelName))
                    messageUtil.reply("bot.count." + channelName, subCount)
                else
                    messageUtil.reply("bot.count.default", subCount.toString)
            case Failure(t) =>
                messageUtil.reply("bot.count.error", t.getMessage)
                twitch.TwitchLogger.error("Error getting twitch sub count", t)
        }
    }

}
