package twitch

import java.util.Optional
import javax.inject.{Inject, Provider}

import common.api.PermissionLevel
import common.api.PermissionLevel.PermissionLevel
import common.models.{CustomCommand, FilterSettings, TwitchRegular}
import common.ref.MtrConfigRef
import net.engio.mbassy.listener.Handler
import org.kitteh.irc.client.library.element.MessageTag
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.event.client.{ClientConnectionEndedEvent, ClientConnectionEstablishedEvent, ClientNegotiationCompleteEvent}
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent
import play.api.cache.SyncCacheApi
import play.api.db.Database
import twitch.api.TwitchChatMessageEvent
import twitch.api.usernotice.UserNoticeMessageId
import twitch.events.{TwitchCheerEvent, TwitchCommandExecutionEvent, TwitchMessageEvent, TwitchSubEvent}
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
    def connected(event: ClientConnectionEstablishedEvent): Unit = {
        twitchBot.get().isConnected = true
        TwitchLogger.info("Monster Truck Bot connected!")
    }

    @Handler
    def connectionEnded(event: ClientConnectionEndedEvent): Unit = {
        twitchBot.get().isConnected = false
        TwitchLogger.info("Monster Truck Bot disconnected!")
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
                    messageSplit.head.substring(mtrConfigRef.commandPrefix.length).toLowerCase,
                    messageSplit.tail
                ))
            }

        if (msgEvent.getTag("bits").isPresent)
            twitchBot.get.client.getEventManager.callEvent(new TwitchCheerEvent(msgEvent.getMessageEvent, msgEvent.getTag("bits").get().getValue.get().toInt))
    }

    @Handler
    def twitchUserNoticeRecived(userNoticeEvent: UserNoticeEvent): Unit = {
        val msgIdTag: Optional[MessageTag] = userNoticeEvent.getTag("msg-id")
        if (msgIdTag.isPresent && msgIdTag.get().getValue.isPresent) {
            val msgId: String = msgIdTag.get().getValue.get()
            if (msgId == UserNoticeMessageId.SUBSCRIPTION.toString ||
                msgId == UserNoticeMessageId.RESUBSCRIPTION.toString ||
                msgId == UserNoticeMessageId.GIFTED_SUBSCRIPTION.toString)
                twitchBot.get.client.getEventManager.callEvent(new TwitchSubEvent(userNoticeEvent))
        }
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
                twitchMessageUtil.sendMessageToChannel(commandEvent.getChannel, customCommandOpt.get.commandContent, commandEvent.getArgs: _*)
        }
    }

    @Handler
    def twitchCheerEvent(twitchCheerEvent: TwitchCheerEvent): Unit = {
        TwitchLogger.debug(s"Cheer! ${twitchCheerEvent.displayName} just cheered ${twitchCheerEvent.getCheerAmount} bits! Message: ${twitchCheerEvent.getMessage}")
        twitchBotCheerEventHandler.handleEvent(twitchCheerEvent)
    }

    @Handler
    def twitchSubEvent(twitchSubEvent: TwitchSubEvent): Unit = {
        TwitchLogger.debug(s"Subscription! ${twitchSubEvent.displayName} just subscribed!")
        var messageVariant: String = twitchSubEvent.channelName.toLowerCase
        if (!twitchMessageUtil.isDefined(s"bot.subMessages.subscription.$messageVariant"))
            messageVariant = "default"
        val displayName: String = if (twitchSubEvent.displayName.startsWith("@"))
            twitchSubEvent.displayName
        else
            "@" + twitchSubEvent.displayName
        twitchSubEvent.msgId match {
            case UserNoticeMessageId.SUBSCRIPTION =>
                twitchMessageUtil.replyToChannel(
                    twitchSubEvent.getChannel,
                    displayName,
                    s"bot.subMessages.subscription.$messageVariant", twitchSubEvent.resubMonthCount.getOrElse(1)
                )
            case UserNoticeMessageId.RESUBSCRIPTION =>
                twitchMessageUtil.replyToChannel(
                    twitchSubEvent.getChannel,
                    displayName,
                    s"bot.subMessages.resubscription.$messageVariant", twitchSubEvent.resubMonthCount.getOrElse(1)
                )
            case UserNoticeMessageId.GIFTED_SUBSCRIPTION =>
                val recipientDisplayName: String = if (twitchSubEvent.recipientDisplayName.startsWith("@"))
                    twitchSubEvent.recipientDisplayName
                else
                    "@" + twitchSubEvent.recipientDisplayName
                twitchMessageUtil.replyToChannel(
                    twitchSubEvent.getChannel,
                    recipientDisplayName,
                    s"bot.subMessages.giftedSub.$messageVariant", displayName
                )
            case UserNoticeMessageId.CHARITY =>
            case _ =>
        }
    }

    def getUserPermissionLevel(twitchEvent: TwitchChatMessageEvent): PermissionLevel = {
        if (twitchEvent.channelName == twitchEvent.nick)
            PermissionLevel.OWNER
        else if (twitchEvent.isMod)
            PermissionLevel.MODERATORS
        else if (TwitchRegular.getByGuildId(mtrConfigRef.guildId).exists(_.twitchUsername.equalsIgnoreCase(twitchEvent.displayName)))
            PermissionLevel.REGULARS
        else if (twitchEvent.isSub)
            PermissionLevel.SUBSCRIBERS
        else
            PermissionLevel.EVERYONE
    }

}
