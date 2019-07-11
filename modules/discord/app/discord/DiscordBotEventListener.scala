package discord

import common.api.PermissionLevel
import common.api.PermissionLevel.PermissionLevel
import common.components.DaoComponents
import common.models.{CustomCommand, FilterSettings}
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.components.DiscordFilterComponents
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.{Inject, Provider}

import scala.collection.JavaConverters._

class DiscordBotEventListener @Inject()(
                                           implicit config: MtrConfigRef,
                                           discordBot: Provider[DiscordBot],
                                           discordCommandRegistry: DiscordCommandRegistry,
                                           filterComponents: DiscordFilterComponents,
                                           daoComponents: DaoComponents,
                                           discordMessageUtil: DiscordMessageUtil
                                       ) {

//    @EventSubscriber
//    def onReadyEvent(event: ReadyEvent): Unit = {
//        DiscordLogger.info("Monster Truck Bot ready")
//        discordBot.get().guild = Option(discordBot.get.futureClient.getGuildByID(config.guildId))
//        discordBot.get().moderatorRole = Option(discordBot.get.futureClient.getRoleByID(config.moderatorRoleId))
//        discordBot.get().regularRole = Option(discordBot.get.futureClient.getRoleByID(config.regularRoleId))
//        discordBot.get().subscriberRole = Option(discordBot.get.futureClient.getRoleByID(config.subscriberRoleId))
//        discordBot.get().futureClient.changeUsername(config.discordUsername)
//        discordBot.get().futureClient.changePresence(StatusType.ONLINE, ActivityType.PLAYING, config.discordGame)
//    }
//
//    @EventSubscriber
//    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
//        implicit val message: IMessage = event.getMessage
//        if (!message.getAuthor.isBot && (message.getChannel.isPrivate || message.getGuild.getLongID == config.guildId)) {
//            if (!filterMessage(message) && message.getContent.startsWith(config.commandPrefix)) {
//                val contentSplit: Array[String] = message.getContent.split("\\s")
//                discordBot.get().futureClient.getDispatcher.dispatch(new DiscordCommandExecutionEvent(
//                    message,
//                    contentSplit.head.substring(config.commandPrefix.length).toLowerCase,
//                    contentSplit.tail,
//                    message.getAuthor
//                ))
//            }
//        }
//    }
//
//    def filterMessage(message: IMessage): Boolean = {
//        var hasBeenFiltered: Boolean = false
//        val filterSettings: Option[FilterSettings] = daoComponents.filterSettingsDAO.get(config.guildId)
//        if (filterSettings.isDefined && !message.getChannel.isPrivate) {
//            val userPermissionLevel: PermissionLevel = getUserPermissionLevel(message.getAuthor)
//            if (filterSettings.get.capsFilterEnabled && userPermissionLevel < filterSettings.get.getCapsFilterExemptionLevel)
//                hasBeenFiltered = hasBeenFiltered || filterComponents.capsFilter.filterMessage(message, filterSettings.get)
//            if (filterSettings.get.linksFilterEnabled && userPermissionLevel < filterSettings.get.getLinksFilterExemptionLevel)
//                hasBeenFiltered = hasBeenFiltered || filterComponents.linksFilter.filterMessage(message, filterSettings.get)
//            if (filterSettings.get.blacklistFilterEnabled && userPermissionLevel < filterSettings.get.getBlackListFilterExemptionLevel)
//                hasBeenFiltered = hasBeenFiltered || filterComponents.blacklistFilter.filterMessage(message, filterSettings.get)
//        }
//        hasBeenFiltered
//    }
//
//    @EventSubscriber
//    def onCommandExecutionEvent(event: DiscordCommandExecutionEvent): Unit = {
//        implicit val message: IMessage = event.getMessage
//        val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
//        val userPermissionLevel: PermissionLevel = getUserPermissionLevel(event.getUser)
//        if (commandOpt.isDefined && userPermissionLevel >= commandOpt.get.permissionLevel) {
//            commandOpt.get.execute(event)
//        } else {
//            val customCommandOpt: Option[CustomCommand] = daoComponents.customCommandDAO.get(config.guildId, event.getCommand)
//            if (customCommandOpt.isDefined && userPermissionLevel >= customCommandOpt.get.getPermissionLevel)
//                discordMessageUtil.replyToMessageWithoutAt(message, customCommandOpt.get.commandContent)
//        }
//    }
//
//    def getUserPermissionLevel(user: IUser): PermissionLevel = {
//        val guild: IGuild = discordBot.get().futureClient.getGuildByID(config.guildId)
//        val userRoleIds = user.getRolesForGuild(guild).asScala.map(_.getLongID)
//        if (user.getLongID == guild.getOwnerLongID)
//            PermissionLevel.OWNER
//        else if (userRoleIds.contains(config.moderatorRoleId))
//            PermissionLevel.MODERATORS
//        else if (userRoleIds.contains(config.regularRoleId))
//            PermissionLevel.REGULARS
//        else if (userRoleIds.contains(config.subscriberRoleId))
//            PermissionLevel.SUBSCRIBERS
//        else
//            PermissionLevel.EVERYONE
//    }

}
