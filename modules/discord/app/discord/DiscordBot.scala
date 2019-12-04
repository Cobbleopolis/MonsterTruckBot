package discord

import ackcord._
import ackcord.data.raw.RawGuild
import ackcord.data.{Guild, GuildId, Role, RoleId}
import ackcord.syntax._
import ackcord.requests.{CreateMessage, GetGuild, GetGuildRoles}
import akka.actor.ActorSystem
import com.google.inject.Inject
import common.ref.MtrConfigRef
import javax.inject.Singleton
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class DiscordBot @Inject()(implicit conf: MtrConfigRef, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle, actorSystem: ActorSystem) {

    var isReady: Boolean = false

    private val clientSettings: ClientSettings = ClientSettings(
        token = conf.discordToken
    )
    var futureClient: Future[DiscordClient] = clientSettings.createClient()

    import clientSettings.executionContext

    val guildId: GuildId = GuildId(conf.guildId)
    DiscordLogger.info(guildId.toString)

    var guild: Option[Guild] = None
    var moderatorRole: Option[Role] = None
    var regularRole: Option[Role] = None
    var subscriberRole: Option[Role] = None

    DiscordLogger.info("TEST")
    futureClient.foreach(client => {
        DiscordLogger.info("TEST 2")
        client.onEvent {
            client.withCache[SourceRequest, APIMessage] { implicit cache => {
                case APIMessage.Ready(_) =>
                    import client.sourceRequesterRunner._
                    DiscordLogger.info("Monster Truck Bot is ready")
                    val x = for {
                        g <- optionPure(guildId.resolve)
                        modRole <- optionPure(g.roleById(RoleId(conf.moderatorRoleId)))
                        regRole <- optionPure(g.roleById(RoleId(conf.regularRoleId)))
                        subRole <- optionPure(g.roleById(RoleId(conf.subscriberRoleId)))
                    } yield (g, modRole, regRole, subRole)
                    for {
                        g <- optionPure(guildId.resolve)
                    } yield ()
                case _ => client.sourceRequesterRunner.unit
            }
            }
        }
        client.login()
    })

    connect()

    lifecycle.addStopHook(() => disconnect())

    def connect(): Unit = {
        DiscordLogger.info("Monster Truck Bot logging in...")
        //        futureClient.login()
        DiscordLogger.info("Monster Truck Bot has begun the login process")
    }

    def disconnect(): Future[Unit] = Future {
        DiscordLogger.info("Monster Truck Bot logging out...")
        //        futureClient.logout()
        //        while (futureClient.isLoggedIn)
        //            Thread.sleep(0)
        DiscordLogger.info("Monster Truck Bot finished logging out")
    }

    def reconnect(): Future[Unit] = disconnect().map(_ => {
        DiscordLogger.info("Monster Truck Bot reconnecting...")
        connect()
        //        while (!futureClient.isReady)
        //            Thread.sleep(0)
        DiscordLogger.info("Monster Truck Bot has reconnected")
    })

    def getInviteLink(redirectTo: String): String = {
        //        val inviteBuilder: BotInviteBuilder = new BotInviteBuilder(futureClient)
        //        inviteBuilder.withPermissions(java.util.EnumSet.of[Permissions](
        //            Permissions.ADMINISTRATOR,
        //            Permissions.KICK,
        //            Permissions.BAN,
        //            Permissions.READ_MESSAGES,
        //            Permissions.SEND_MESSAGES,
        //            Permissions.EMBED_LINKS,
        //            Permissions.MENTION_EVERYONE
        //        ))
        //        inviteBuilder.build() + "&guild_id=" + java.lang.Long.toUnsignedString(conf.guildId) + "&response_type=code&redirect_uri=" + URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.name())
        redirectTo //TODO Fix this shit!
    }
}