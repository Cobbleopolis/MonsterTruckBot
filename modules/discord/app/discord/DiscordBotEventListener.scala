package discord

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.api.PermissionLevel
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.filters.{DiscordBlacklistFilter, DiscordCapsFilter, DiscordLinksFilter}
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.i18n.MessagesApi
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.{IGuild, IMessage, IUser}

import scala.collection.JavaConverters._

class DiscordBotEventListener @Inject()(implicit config: MtrConfigRef, discordBot: Provider[DiscordBot], messages: MessagesApi, discordCommandRegistry: DiscordCommandRegistry, capsFilter: DiscordCapsFilter, linksFilter: DiscordLinksFilter, blacklistFilter: DiscordBlacklistFilter, database: Database, cache: SyncCacheApi) {

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(config.discordUsername)
        discordBot.get().client.changePlayingText(config.discordGame)
    }

    @EventSubscriber
    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        if (!message.getAuthor.isBot)
            if (message.getContent.startsWith(config.commandPrefix)) {
                val contentSplit: Array[String] = message.getContent.split("\\s")
                discordBot.get().client.getDispatcher.dispatch(new CommandExecutionEvent(
                    message,
                    contentSplit.head.substring(config.commandPrefix.length),
                    contentSplit.tail,
                    message.getAuthor
                ))
            } else if (!message.getChannel.isPrivate && message.getGuild.getLongID == config.guildId) {
                val filterSettings: Option[FilterSettings] = FilterSettings.get(message.getGuild.getLongID)
                if (message.getAuthor.getLongID != message.getGuild.getOwnerLongID
                    && !message.getAuthor.getRolesForGuild(message.getGuild).asScala.map(_.getLongID).contains(config.moderatorRoleId)) // See if the mods want to be able to filter themselves
                    filterMessage(message, filterSettings)
            }
    }

    def filterMessage(message: IMessage, filterSettings: Option[FilterSettings]): Unit = {
        if (filterSettings.isDefined) {
            val userPermissionLevel: PermissionLevel = getUserPermissionLevel(message.getAuthor)
            if (filterSettings.get.capsFilterEnabled && userPermissionLevel < filterSettings.get.getCapsFilterExemptionLevel)
                capsFilter.filterMessage(message, filterSettings.get)
            if (filterSettings.get.linksFilterEnabled && userPermissionLevel < filterSettings.get.getLinksFilterExemptionLevel)
                linksFilter.filterMessage(message, filterSettings.get)
            if (filterSettings.get.blacklistFilterEnabled && userPermissionLevel < filterSettings.get.getBlackListFilterExemptionLevel)
                blacklistFilter.filterMessage(message, filterSettings.get)
        }
    }

    @EventSubscriber
    def onCommandExecutionEvent(event: CommandExecutionEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
        val userPermissionLevel: PermissionLevel = getUserPermissionLevel(event.getUser)
        if (commandOpt.isDefined && userPermissionLevel >= commandOpt.get.permissionLevel) {
            commandOpt.get.execute(event)
        } else {
            val customCommandOpt: Option[CustomCommand] = CustomCommand.get(config.guildId, event.getCommand)
            if (customCommandOpt.isDefined && userPermissionLevel >= customCommandOpt.get.getPermissionLevel)
                message.getChannel.sendMessage(customCommandOpt.get.commandContent)
        }
    }

    def getUserPermissionLevel(user: IUser): PermissionLevel = {
        val guild: IGuild = discordBot.get().client.getGuildByID(config.guildId)
        val userRoleIds = user.getRolesForGuild(guild).asScala.map(_.getLongID)
        if (user.getLongID == guild.getOwnerLongID)
            PermissionLevel.OWNER
        else if (userRoleIds.contains(config.moderatorRoleId))
            PermissionLevel.MODERATORS
        else if (userRoleIds.contains(config.subscriberRoleId))
            PermissionLevel.SUBSCRIBERS
        else
            PermissionLevel.EVERYONE
    }

}
