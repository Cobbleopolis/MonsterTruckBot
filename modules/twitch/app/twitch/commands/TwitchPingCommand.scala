package twitch.commands

import javax.inject.Inject

import common.api.commands.PingCommand
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchPingCommand @Inject()(val messageUtil: TwitchMessageUtil) extends TwitchCommand with PingCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = messageUtil.reply("bot.ping")

}
