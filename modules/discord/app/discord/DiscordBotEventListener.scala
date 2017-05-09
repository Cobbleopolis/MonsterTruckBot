package discord

import javax.inject.{Inject, Provider}

import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import play.api.Configuration
import play.api.i18n.MessagesApi
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}
import sx.blah.discord.handle.obj.{IMessage, Status}

class DiscordBotEventListener @Inject()(implicit configuration: Configuration, discordBot: Provider[DiscordBot], messages: MessagesApi, discordCommandRegistry: DiscordCommandRegistry) {

    val commandPrefix: String = configuration.getString("mtr.commandPrefix").getOrElse("!")

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(configuration.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot"))
        discordBot.get().client.changeStatus(Status.game(configuration.getString("mtrBot.discord.game").getOrElse("Hap! Hap! Hap!")))
    }

    @EventSubscriber
    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        if (message.getContent.startsWith(commandPrefix) && !message.getAuthor.isBot) {
            val contentSplit: Array[String] = message.getContent.split("\\s")
            discordBot.get().client.getDispatcher.dispatch(new CommandExecutionEvent(
                message,
                contentSplit.head.substring(commandPrefix.length),
                contentSplit.tail,
                message.getAuthor
            ))
        }
    }

    @EventSubscriber
    def onCommandExecutionEvent(event: CommandExecutionEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
        if (commandOpt.isDefined)
            commandOpt.get.execute(event)
    }

}
