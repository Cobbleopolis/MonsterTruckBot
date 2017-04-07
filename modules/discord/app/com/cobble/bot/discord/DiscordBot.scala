package com.cobble.bot.discord

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Singleton

import com.google.inject.Inject
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.BotInviteBuilder

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DiscordBot @Inject()(implicit configuration: Configuration, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle, context: ExecutionContext) {

    private val token: Option[String] = configuration.getString("mtrBot.discord.token")
    private val clientBuilder: ClientBuilder = new ClientBuilder().setDaemon(true)
    var client: IDiscordClient = _

    if (token.isDefined) {
        clientBuilder.withToken(token.get)
        client = clientBuilder.login()
        val dispatcher: EventDispatcher = client.getDispatcher
        dispatcher.registerListener(eventListener)
        lifecycle.addStopHook(() => Future {
            DiscordLogger.info("Monster Truck Bot logging out")
            client.logout()
        })
    }

    def getInviteLink(guildId: String, redirectTo: String): String = {
        val inviteBuilder: BotInviteBuilder = new BotInviteBuilder(client)
        inviteBuilder.withPermissions(java.util.EnumSet.of[Permissions](
            Permissions.ADMINISTRATOR,
            Permissions.KICK,
            Permissions.BAN,
            Permissions.READ_MESSAGES,
            Permissions.SEND_MESSAGES,
            Permissions.EMBED_LINKS,
            Permissions.ATTACH_FILES,
            Permissions.MENTION_EVERYONE,
            Permissions.VOICE_CONNECT,
            Permissions.VOICE_SPEAK
        ))
        inviteBuilder.build() + "&guild_id=" + guildId + "&response_type=code&redirect_uri=" + URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.name())
    }
}