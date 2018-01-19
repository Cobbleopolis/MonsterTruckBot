package twitch.api

import common.api.Command
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

trait TwitchCommand extends Command {

    val messageUtil: TwitchMessageUtil

    def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit
}
