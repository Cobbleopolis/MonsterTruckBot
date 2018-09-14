package twitch.commands

import buildinfo.BuildInfo
import common.api.commands.VersionCommand
import javax.inject.Inject
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchVersionCommand @Inject()(val messageUtil: TwitchMessageUtil) extends TwitchCommand with VersionCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = messageUtil.reply(BuildInfo.version)

}
