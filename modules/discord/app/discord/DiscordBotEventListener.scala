package discord

import common.components.DaoComponents
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.components.DiscordFilterComponents
import discord.event.DiscordCommandExecutionEvent
import discord.util.{DiscordMessageUtil, DiscordUtil}
import discord4j.core.`object`.entity.{Channel, Message}
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import javax.inject.{Inject, Provider}
import org.reactivestreams.Publisher
import reactor.core.scala.publisher._

import scala.compat.java8.OptionConverters._

class DiscordBotEventListener @Inject()(
                                           implicit config: MtrConfigRef,
                                           discordBot: Provider[DiscordBot],
                                           discordCommandRegistry: DiscordCommandRegistry,
                                           filterComponents: DiscordFilterComponents,
                                           daoComponents: DaoComponents,
                                           discordMessageUtil: DiscordMessageUtil,
                                           discordUtil: DiscordUtil
                                       ) {

    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().guild = discordBot.get.botClient.getGuildById(discordBot.get.guildSnowflake).blockOptional
        discordBot.get().moderatorRole = discordBot.get.botClient.getRoleById(discordBot.get().guildSnowflake, discordBot.get().moderatorRoleSnowflake).blockOptional
        discordBot.get().regularRole = discordBot.get.botClient.getRoleById(discordBot.get().guildSnowflake, discordBot.get().regularRoleSnowflake).blockOptional
        discordBot.get().subscriberRole = discordBot.get.botClient.getRoleById(discordBot.get().guildSnowflake, discordBot.get().subscriberRoleSnowflake).blockOptional
    }

    def baseTransform(messageEvent: Flux[MessageCreateEvent]): Flux[MessageCreateEvent] = {
        messageEvent.filter { me: MessageCreateEvent =>
            val author = me.getMessage.getAuthor
            author.isDefined && !author.get.isBot
        }.filter { me: MessageCreateEvent =>
            val t: Channel.Type = me.getMessage.getChannel.block.getType
            t == Channel.Type.DM || t == Channel.Type.GROUP_DM || t == Channel.Type.GUILD_TEXT
        }
    }

    def onMessageCreate(messageEvent: Flux[MessageCreateEvent]): Publisher[Any] = {
        messageEvent.map[Message](_.getMessage)
            .filter{ msg =>
                val t: Channel.Type = msg.getChannel.block().getType
                t == Channel.Type.DM || t == Channel.Type.GROUP_DM || t == Channel.Type.GUILD_TEXT
            }.filter((msg: Message) => msg.getContent.asScala.exists(_.startsWith(config.commandPrefix)))
            .map[DiscordCommandExecutionEvent] { msg =>
                val contentSplit: Array[String] = msg.getContent.asScala.getOrElse("").split("\\s")
                new DiscordCommandExecutionEvent(
                    msg,
                    contentSplit.headOption.map(_.substring(config.commandPrefix.length).toLowerCase).getOrElse(""),
                    contentSplit.tail,
                    msg.getAuthor.get()
                )
            }.flatMap[Any] { event: DiscordCommandExecutionEvent =>
                val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
                if (commandOpt.isDefined
                    && discordUtil.getUserPermissionLevel(event.getUser) >= commandOpt.get.permissionLevel)
                    commandOpt.get.execute(event)
                else {
                    val customCommandOpt = daoComponents.customCommandDAO.get(config.guildId, event.getCommand)
                    if (customCommandOpt.isDefined
                        && discordUtil.getUserPermissionLevel(event.getUser) >= customCommandOpt.get.getPermissionLevel)
                        discordMessageUtil.replyToMessageWithoutAt(event.getMessage, customCommandOpt.get.commandContent)
                    else
                        Mono.empty
                }
            }
    }

    //    def onCommandExecution(messageEvent: Flux[MessageCreateEvent]): Publisher[Message] = {
    //        messageEvent.map[Message](_.getMessage)
    //            .filter((msg: Message) => msg.getContent.asScala.exists(_.startsWith(config.commandPrefix)))
    //            .flatMap[Message]((msg: Message) => msg.getChannel.block().createMessage("pong!"))
    //    }

    //    @EventSubscriber
    //    def onMessageReceivedEvent(event: MessageCreateEvent): Unit = {
    //        implicit val message: IMessage = event.getMessage
    //        if (!message.getAuthor.isBot && (message.getChannel.isPrivate || message.getGuild.getLongID == config.guildId)) {
    //            if (!filterMessage(message) && message.getContent.startsWith(config.commandPrefix)) {
    //                val contentSplit: Array[String] = message.getContent.split("\\s")
    //                discordBot.get().botClient.getDispatcher.dispatch(new DiscordCommandExecutionEvent(
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


}
