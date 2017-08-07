package twitch.commands

import javax.inject.Inject

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.commands.SubCountCommand
import com.cobble.bot.common.ref.{MessageRef, MtrConfigRef}
import com.cobble.bot.common.util.TwitchApiUtil
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class TwitchSubCountCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, twitchApiUtil: TwitchApiUtil, mtrConfigRef: MtrConfigRef, executionContext: ExecutionContext) extends TwitchCommand with SubCountCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        val channelName: String = executionEvent.getChannel.getName.substring(1)
        getTotalSubCount(mtrConfigRef.twitchChannels.get(channelName), twitchApiUtil).onComplete {
            case Success(subCount) =>
                if (messageUtil.isDefined("bot.count." + channelName))
                    messageUtil.replyMe("bot.count." + channelName, subCount)
                else
                    messageUtil.replyMe("bot.count.default", subCount.toString)
            case Failure(t) =>
                messageUtil.reply("bot.count.error", t.getMessage.substring(0, Math.min(t.getMessage.length, MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE - 50))) //Subtract 50 for the error prefix.
                twitch.TwitchLogger.error("Error getting twitch sub count", t)
        }
    }

}
