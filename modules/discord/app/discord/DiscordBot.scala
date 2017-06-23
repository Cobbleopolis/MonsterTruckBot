package discord

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Singleton

import com.cobble.bot.common.ref.MtrConfigRef
import com.google.inject.Inject
import play.api.inject.ApplicationLifecycle
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.BotInviteBuilder

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DiscordBot @Inject()(implicit conf: MtrConfigRef, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle, context: ExecutionContext) {

    private val clientBuilder: ClientBuilder = new ClientBuilder().setDaemon(true)
    var client: IDiscordClient = _

    if (conf.discordToken.isDefined) {
        clientBuilder.withToken(conf.discordToken.get)
        client = clientBuilder.login()
        val dispatcher: EventDispatcher = client.getDispatcher
        dispatcher.registerListener(eventListener)
        lifecycle.addStopHook(() => Future {
            DiscordLogger.info("Monster Truck Bot logging out...")
            client.logout()
            DiscordLogger.info("Monster Truck Bot finished logging out")
        })
    } else {
        DiscordLogger.logger.error("Discord token not found in config")
    }

    def getInviteLink(redirectTo: String): String = {
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
        inviteBuilder.build() + "&guild_id=" + conf.discordClientId.get + "&response_type=code&redirect_uri=" + URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.name())
    }
}