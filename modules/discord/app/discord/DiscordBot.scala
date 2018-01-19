package discord

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Singleton

import common.ref.MtrConfigRef
import com.google.inject.Inject
import play.api.inject.ApplicationLifecycle
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}
import sx.blah.discord.handle.obj.{IGuild, IRole, Permissions}
import sx.blah.discord.modules.Configuration
import sx.blah.discord.util.BotInviteBuilder

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DiscordBot @Inject()(implicit conf: MtrConfigRef, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle, context: ExecutionContext) {

    Configuration.AUTOMATICALLY_ENABLE_MODULES = false
    Configuration.LOAD_EXTERNAL_MODULES = false

    private val clientBuilder: ClientBuilder = new ClientBuilder().setDaemon(true)
    var client: IDiscordClient = _

    var guild: Option[IGuild] = None
    var moderatorRole: Option[IRole] = None
    var regularRole: Option[IRole] = None
    var subscriberRole: Option[IRole] = None

    clientBuilder.withToken(conf.discordToken)
    client = clientBuilder.login()
    val dispatcher: EventDispatcher = client.getDispatcher
    dispatcher.registerListener(eventListener)
    lifecycle.addStopHook(() => Future {
        DiscordLogger.info("Monster Truck Bot logging out...")
        client.logout()
        DiscordLogger.info("Monster Truck Bot finished logging out")
    })

    def getInviteLink(redirectTo: String): String = {
        val inviteBuilder: BotInviteBuilder = new BotInviteBuilder(client)
        inviteBuilder.withPermissions(java.util.EnumSet.of[Permissions](
            Permissions.ADMINISTRATOR,
            Permissions.KICK,
            Permissions.BAN,
            Permissions.READ_MESSAGES,
            Permissions.SEND_MESSAGES,
            Permissions.EMBED_LINKS,
            Permissions.MENTION_EVERYONE
        ))
        inviteBuilder.build() + "&guild_id=" + java.lang.Long.toUnsignedString(conf.guildId) + "&response_type=code&redirect_uri=" + URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.name())
    }
}