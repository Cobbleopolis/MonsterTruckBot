package twitch

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.ref.MtrConfigRef
import net.engio.mbassy.listener.Handler
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.event.client.ClientConnectedEvent
import twitch.events.TwitchMessageEvent

class TwitchBotEventListener @Inject()(twitchBot: Provider[TwitchBot], mtrConfigRef: MtrConfigRef) {

    @Handler
    def connected(event: ClientConnectedEvent): Unit = {
        TwitchLogger.info("Monster Truck Bot connected!")
    }

    @Handler
    def messageReceived(msgEvent: ChannelMessageEvent): Unit =
        if (msgEvent.getActor.getNick != mtrConfigRef.twitchUsername)
            twitchBot.get.client.getEventManager.callEvent(new TwitchMessageEvent(msgEvent))

    @Handler
    def twitchMessageReceived(msgEvent: TwitchMessageEvent): Unit = {
        if (msgEvent.getMessage.startsWith(mtrConfigRef.commandPrefix + "ping"))
            msgEvent.sendReply("pong!")
    }

}
