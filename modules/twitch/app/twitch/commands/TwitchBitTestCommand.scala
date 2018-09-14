package twitch.commands

import common.api.commands.BitTestCommand
import javax.inject.{Inject, Provider}
import twitch.TwitchBot
import twitch.api.TwitchCommand
import twitch.events.{TwitchCheerEvent, TwitchCommandExecutionEvent}
import twitch.util.TwitchMessageUtil

class TwitchBitTestCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, twitchBot: Provider[TwitchBot]) extends TwitchCommand with BitTestCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        val amountStr: String = executionEvent.getArgs.headOption.getOrElse("100")
        try {
            val amountInt: Int = Integer.parseInt(amountStr)
            twitchBot.get.client.getEventManager.callEvent(new TwitchCheerEvent(executionEvent.getMessageEvent, amountInt))
            messageUtil.reply("bot.bitTesting.bitsAdded", amountInt)
        } catch {
            case _: NumberFormatException => messageUtil.reply("bot.bitTesting.notValidInt", amountStr)
            case e: Exception => messageUtil.reply("bot.commandExecutionError", e.getMessage)
        }
    }

}
