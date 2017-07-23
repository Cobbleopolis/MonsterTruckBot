package twitch

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.api.PermissionLevel
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.ref.MtrConfigRef
import net.engio.mbassy.listener.Handler
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.event.client.ClientConnectedEvent
import twitch.api.TwitchEvent
import twitch.events.{TwitchCommandExecutionEvent, TwitchMessageEvent}

class TwitchBotEventListener @Inject()(twitchBot: Provider[TwitchBot], mtrConfigRef: MtrConfigRef, twitchCommandRegistry: TwitchCommandRegistry) {

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
        if (msgEvent.getMessage.startsWith(mtrConfigRef.commandPrefix)) {
            val messageSplit: Array[String] = msgEvent.getMessage.split("\\s")
            twitchBot.get.client.getEventManager.callEvent(new TwitchCommandExecutionEvent(
                msgEvent.getMessageEvent,
                messageSplit.head.substring(mtrConfigRef.commandPrefix.length),
                messageSplit.tail
            ))
        }
    }

    @Handler
    def twitchCommandExecuted(commandEvent: TwitchCommandExecutionEvent): Unit = {
        val commandOpt = twitchCommandRegistry.commands.get(commandEvent.getCommand)
        if (commandOpt.isDefined && getUserPermissionLevel(commandEvent) >= commandOpt.get.permissionLevel)
            commandOpt.get.execute(commandEvent)
    }

    def getUserPermissionLevel(twitchEvent: TwitchEvent): PermissionLevel = {
        if (mtrConfigRef.twitchChannels.contains("#" + twitchEvent.getActor.getNick))
            PermissionLevel.OWNER
        else if (twitchEvent.isMod)
            PermissionLevel.MODERATORS
        else if (twitchEvent.isSub)
            PermissionLevel.SUBSCRIBERS
        else
            PermissionLevel.EVERYONE
    }

}
