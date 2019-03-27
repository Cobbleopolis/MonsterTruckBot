package discord

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.fasterxml.jackson.annotation.{JsonAutoDetect, PropertyAccessor}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.google.inject.Inject
import common.ref.MtrConfigRef
import discord4j.common.jackson.{PossibleModule, UnknownPropertyHandler}
import discord4j.core.`object`.entity.{Guild, Role}
import discord4j.core.`object`.presence.{Activity, Presence}
import discord4j.core.`object`.util.{Permission, PermissionSet, Snowflake}
import discord4j.core.event.EventDispatcher
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.{DiscordClient, DiscordClientBuilder}
import discord4j.rest.RestClient
import discord4j.rest.http.ExchangeStrategies
import discord4j.rest.http.client.DiscordWebClient
import discord4j.rest.request.{DefaultRouter, Router}
import javax.inject.Singleton
import play.api.inject.ApplicationLifecycle
import reactor.core.Disposable
import reactor.core.scala.publisher._
import reactor.core.scheduler.Schedulers
import reactor.netty.http.client.HttpClient

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DiscordBot @Inject()(implicit conf: MtrConfigRef, eventListener: DiscordBotEventListener, lifecycle: ApplicationLifecycle, context: ExecutionContext) {

    //    Hooks.onOperatorDebug()

    private val clientBuilder: DiscordClientBuilder = new DiscordClientBuilder(conf.discordToken)
        .setInitialPresence(Presence.online(Activity.playing(conf.discordGame)))

    private var loginDisposable: Option[Disposable] = None

    val guildSnowflake: Snowflake = Snowflake.of(conf.guildId)
    val moderatorRoleSnowflake: Snowflake = Snowflake.of(conf.moderatorRoleId)
    val regularRoleSnowflake: Snowflake = Snowflake.of(conf.regularRoleId)
    val subscriberRoleSnowflake: Snowflake = Snowflake.of(conf.subscriberRoleId)
    val maintainerUserSnowflake: Option[Snowflake] = conf.maintainerUserId.map(Snowflake.of)

    var guild: Option[Guild] = None
    var moderatorRole: Option[Role] = None
    var regularRole: Option[Role] = None
    var subscriberRole: Option[Role] = None
    var botClient: DiscordClient = clientBuilder.build()

    private val mapper: ObjectMapper = new ObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .addHandler(new UnknownPropertyHandler(true))
        .registerModules(new PossibleModule, new Jdk8Module)
    val webClient: DiscordWebClient = new DiscordWebClient(HttpClient.create().compress(true),
        ExchangeStrategies.jackson(mapper), conf.discordToken)
    private val router: Router = new DefaultRouter(webClient, Schedulers.elastic(), Schedulers.elastic())
    val restClient: RestClient = new RestClient(router)

    private val dispatcher: EventDispatcher = botClient.getEventDispatcher

    setupEventListeners()
    connect()

    lifecycle.addStopHook(() => disconnect())

    def setupEventListeners(): Unit = {
        dispatcher.on(classOf[ReadyEvent]).log()
            .subscribe(e => eventListener.onReadyEvent(e))

        dispatcher.on(classOf[MessageCreateEvent])
            .transform[MessageCreateEvent](eventListener.baseTransform)
            .transform[Any](eventListener.onMessageCreate)
            .subscribe()
    }

    def connect(): Unit = {
        DiscordLogger.info("Monster Truck Bot logging in...")
        loginDisposable = Some(botClient.login().subscribe())
        DiscordLogger.info("Monster Truck Bot has begun the login process")
    }

    def disconnect(): Future[Unit] = Future {
        DiscordLogger.info("Monster Truck Bot logging out...")
        if (loginDisposable.isDefined)
            loginDisposable.get.dispose()
        botClient.logout()
        while (botClient.isConnected)
            Thread.sleep(0)
        DiscordLogger.info("Monster Truck Bot finished logging out")
    }

    def reconnect(): Future[Unit] = disconnect().map(_ => {
        DiscordLogger.info("Monster Truck Bot reconnecting...")
        connect()
        while (!botClient.isConnected)
            Thread.sleep(0)
        DiscordLogger.info("Monster Truck Bot has reconnected")
    })

    def getInviteLink(redirectTo: String): String = {
        val permissions: PermissionSet = PermissionSet.of(
            Permission.ADMINISTRATOR,
            Permission.KICK_MEMBERS,
            Permission.BAN_MEMBERS,
            Permission.READ_MESSAGE_HISTORY,
            Permission.SEND_MESSAGES,
            Permission.EMBED_LINKS,
            Permission.MENTION_EVERYONE
        )
        "https://discordapp.com/oauth2/authorize" +
            s"?client_id=${conf.discordClientId}" +
            s"&scope=bot&permissions=${permissions.getRawValue}" +
            s"&guild_id=${java.lang.Long.toUnsignedString(conf.guildId)}" +
            s"&redirect_uri=${URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.name())}"
    }
}