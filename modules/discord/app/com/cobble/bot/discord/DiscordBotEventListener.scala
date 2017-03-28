package com.cobble.bot.discord

import javax.inject.{Inject, Provider}

import buildinfo.BuildInfo
import com.cobble.bot.discord.event.CommandExecutionEvent
import com.cobble.bot.discord.util.DiscordMessageUtil
import play.api.Configuration
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}
import sx.blah.discord.handle.obj.{IMessage, Status}

class DiscordBotEventListener @Inject()(configuration: Configuration, discordBot: Provider[DiscordBot]) {

    val commandPrefix: String = configuration.getString("mtr.commandPrefix").getOrElse("!")

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(configuration.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot"))
        discordBot.get().client.changeStatus(Status.game(configuration.getString("mtrBot.discord.game").getOrElse("Hap Hap Hap")))
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
        event.getCommand match {
            case "ping" =>
                DiscordMessageUtil.reply("pong!")
            case "version" =>
                DiscordMessageUtil.reply(BuildInfo.version)
        }
    }

}
