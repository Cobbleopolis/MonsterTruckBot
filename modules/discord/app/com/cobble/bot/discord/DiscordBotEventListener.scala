package com.cobble.bot.discord

import javax.inject.{Inject, Provider}

import com.cobble.bot.discord.util.DiscordMessageUtil
import play.api.Configuration
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}
import sx.blah.discord.handle.obj.{IMessage, Status}

class DiscordBotEventListener @Inject()(configuration: Configuration, discordBot: Provider[DiscordBot]) {

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(configuration.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot"))
        discordBot.get().client.changeStatus(Status.game(configuration.getString("mtrBot.discord.game").getOrElse("Hap Hap Hap")))
    }

    @EventSubscriber
    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        if (!message.getAuthor.isBot)
            if (message.getContent.startsWith("!ping"))
                DiscordMessageUtil.reply("pong!")
    }

}
