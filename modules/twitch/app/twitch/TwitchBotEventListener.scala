package twitch

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.api.PermissionLevel
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.{MessageRef, MtrConfigRef}
import net.engio.mbassy.listener.Handler
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.event.client.ClientConnectedEvent
import play.api.cache.SyncCacheApi
import play.api.db.Database
import twitch.api.TwitchEvent
import twitch.events.{TwitchCheerEvent, TwitchCommandExecutionEvent, TwitchMessageEvent}
import twitch.filters.{TwitchBlacklistFilter, TwitchCapsFilter, TwitchLinksFilter}
import twitch.util.TwitchMessageUtil

class TwitchBotEventListener @Inject()(
                                          implicit twitchBot: Provider[TwitchBot],
                                          mtrConfigRef: MtrConfigRef,
                                          twitchCommandRegistry: TwitchCommandRegistry,
                                          db: Database,
                                          cache: SyncCacheApi,
                                          capsFilter: TwitchCapsFilter,
                                          linksFilter: TwitchLinksFilter,
                                          blacklistFilter: TwitchBlacklistFilter,
                                          twitchMessageUtil: TwitchMessageUtil,
                                          twitchBotCheerEventHandler: TwitchBotCheerEventHandler
                                      ) {

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
        if (!filterMessage(msgEvent))
            if (msgEvent.getMessage.startsWith(mtrConfigRef.commandPrefix)) {
                val messageSplit: Array[String] = msgEvent.getMessage.split("\\s")
                twitchBot.get.client.getEventManager.callEvent(new TwitchCommandExecutionEvent(
                    msgEvent.getMessageEvent,
                    messageSplit.head.substring(mtrConfigRef.commandPrefix.length),
                    messageSplit.tail
                ))
            }

        if(msgEvent.getTag("bits").isPresent)
            twitchBot.get.client.getEventManager.callEvent(new TwitchCheerEvent(msgEvent.getMessageEvent, msgEvent.getTag("bits").get().getValue.get().toInt))
    }

    def filterMessage(message: TwitchMessageEvent): Boolean = {
        var hasBeenFiltered: Boolean = false
        val filterSettings: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
        if (filterSettings.isDefined) {
            val userPermissionLevel: PermissionLevel = getUserPermissionLevel(message)
            if (filterSettings.get.capsFilterEnabled && userPermissionLevel < filterSettings.get.getCapsFilterExemptionLevel)
                hasBeenFiltered = hasBeenFiltered || capsFilter.filterMessage(message, filterSettings.get)
            if (filterSettings.get.linksFilterEnabled && userPermissionLevel < filterSettings.get.getLinksFilterExemptionLevel)
                hasBeenFiltered = hasBeenFiltered || linksFilter.filterMessage(message, filterSettings.get)
            if (filterSettings.get.blacklistFilterEnabled && userPermissionLevel < filterSettings.get.getBlackListFilterExemptionLevel)
                hasBeenFiltered = hasBeenFiltered || blacklistFilter.filterMessage(message, filterSettings.get)
        }
        hasBeenFiltered
    }

    @Handler
    def twitchCommandExecuted(commandEvent: TwitchCommandExecutionEvent): Unit = {
        val commandOpt = twitchCommandRegistry.commands.get(commandEvent.getCommand)
        if (commandOpt.isDefined && getUserPermissionLevel(commandEvent) >= commandOpt.get.permissionLevel)
            commandOpt.get.execute(commandEvent)
        else {
            val customCommandOpt: Option[CustomCommand] = CustomCommand.get(mtrConfigRef.guildId, commandEvent.getCommand)
            if (customCommandOpt.isDefined && getUserPermissionLevel(commandEvent) >= customCommandOpt.get.getPermissionLevel)
                twitchMessageUtil.replyToMessageWithMe(commandEvent.getMessageEvent, customCommandOpt.get.commandContent, commandEvent.getArgs: _*)
        }
    }

    @Handler
    def twitchCheerEvent(twitchCheerEvent: TwitchCheerEvent): Unit = {
        TwitchLogger.debug(s"Cheer! ${twitchCheerEvent.displayName} just cheered ${twitchCheerEvent.getCheerAmount} bits! Message: ${twitchCheerEvent.getMessage}")
        twitchBotCheerEventHandler.handleEvent(twitchCheerEvent)
    }

    def getUserPermissionLevel(twitchEvent: TwitchEvent): PermissionLevel = {
        if (twitchEvent.channelName == twitchEvent.getActor.getNick)
            PermissionLevel.OWNER
        else if (twitchEvent.isMod)
            PermissionLevel.MODERATORS
        else if (twitchEvent.isSub)
            PermissionLevel.SUBSCRIBERS
        else
            PermissionLevel.EVERYONE
    }

}
