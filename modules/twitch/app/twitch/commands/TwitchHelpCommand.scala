package twitch.commands

import common.api.commands.HelpCommand
import common.ref.MtrConfigRef
import javax.inject.Inject
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchHelpCommand @Inject()(val messageUtil: TwitchMessageUtil, mtrConfigRef: MtrConfigRef) extends TwitchCommand with HelpCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = messageUtil.reply("bot.help.message", mtrConfigRef.helpLink)

}
