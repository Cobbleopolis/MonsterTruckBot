package twitch.commands

import javax.inject.Inject

import buildinfo.BuildInfo
import com.cobble.bot.common.api.commands.VersionCommand
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchVersionCommand @Inject()(val messageUtil: TwitchMessageUtil) extends TwitchCommand with VersionCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = messageUtil.reply(BuildInfo.version)

}
