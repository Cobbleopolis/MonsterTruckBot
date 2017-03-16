package com.cobble.bot.discord

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}

import scala.concurrent.Future

@Singleton
class DiscordBot @Inject()(configuration: Configuration, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle) {

    private val token: Option[String] = configuration.getString("mtrBot.discord.token")
    private val clientBuilder: ClientBuilder = new ClientBuilder()
    var client: IDiscordClient = _

    if (token.isDefined) {
        clientBuilder.withToken(token.get)
        client = clientBuilder.login()
        val dispatcher: EventDispatcher = client.getDispatcher
        dispatcher.registerListener(eventListener)
        lifecycle.addStopHook(() => Future.successful(() => {
            DiscordLogger.info("Monster Truck Bot logging out")
            client.logout()
        }))
    }
}