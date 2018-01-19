package twitch

import javax.inject.{Inject, Singleton}

import common.ref.MtrConfigRef
import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.feature.twitch.{TwitchDelaySender, TwitchListener}
import play.api.db.Database
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TwitchBot @Inject()(implicit mtrConfigRef: MtrConfigRef, lifecycle: ApplicationLifecycle, ex: ExecutionContext, db: Database, twitchBotEventListener: TwitchBotEventListener) {

    var isConnected: Boolean = false

    private val clientBuilder: Client.Builder = Client.builder()
        .serverHost("irc.chat.twitch.tv")
        .nick(mtrConfigRef.twitchUsername)
        .serverPassword(s"oauth:${mtrConfigRef.twitchOauth}")
        .messageSendingQueueSupplier((client) => new TwitchDelaySender(client, "TwitchRateLimiter", TwitchDelaySender.MOD_OP_PER_THIRTY_SECONDS))

    clientBuilder.inputListener((line: String) => TwitchLogger.trace("[I] " + line))
    clientBuilder.outputListener((line: String) => TwitchLogger.trace("[O] " + line))

    val client: Client = clientBuilder.build()
    client.getEventManager.registerEventListener(new TwitchListener(client))
    client.getEventManager.registerEventListener(twitchBotEventListener)
//    client.getCapabilityManager.getSupportedCapabilities.forEach(capability => println("Capability: " + capability))
    client.addChannel(mtrConfigRef.twitchChannels.values.toSeq.map(_.ircChannelName): _*)
//    mtrConfigRef.twitchChannels.foreach(client.sendMessage(_, "/me Hello, World!"))
    client.connect()

    lifecycle.addStopHook(() => Future {
        TwitchLogger.info("Monster Truck Bot shutting down...")
        client.shutdown("Application is stopping")
        TwitchLogger.info("Monster Truck Bot finished shutting down")
    })
}
