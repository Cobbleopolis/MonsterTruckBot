package discord

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.models.{CoreSettings, FilterSettings}
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.filters.DiscordCapsFilter
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.MessagesApi
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}
import sx.blah.discord.handle.obj.{IMessage, Status}

import collection.JavaConverters._

class DiscordBotEventListener @Inject()(implicit configuration: Configuration, discordBot: Provider[DiscordBot], messages: MessagesApi, discordCommandRegistry: DiscordCommandRegistry, capsFilter: DiscordCapsFilter, database: Database) {

    val commandPrefix: String = configuration.getString("mtr.commandPrefix").getOrElse("!")

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(configuration.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot"))
        discordBot.get().client.changeStatus(Status.game(configuration.getString("mtrBot.discord.game").getOrElse("Hap! Hap! Hap!")))
    }

    @EventSubscriber
    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        if (!message.getAuthor.isBot)
            if (message.getContent.startsWith(commandPrefix)) {
                val contentSplit: Array[String] = message.getContent.split("\\s")
                discordBot.get().client.getDispatcher.dispatch(new CommandExecutionEvent(
                    message,
                    contentSplit.head.substring(commandPrefix.length),
                    contentSplit.tail,
                    message.getAuthor
                ))
            } else {
                val coreSettings: Option[CoreSettings] = CoreSettings.get(message.getGuild.getID)
                val filterSettings: Option[FilterSettings] = FilterSettings.get(message.getGuild.getID)
                if (coreSettings.isDefined && message.getAuthor.getID != message.getGuild.getOwnerID) {
                    if (coreSettings.get.moderatorRoleId.isDefined)
                        if (!message.getAuthor.getRolesForGuild(message.getGuild).asScala.map(_.getID).contains(coreSettings.get.moderatorRoleId.get))
                            filterMessage(message, filterSettings)
                    else
                        filterMessage(message, filterSettings)
                }
                //TODO figure out what mods want about warnings/bans/things
            }
    }

    def filterMessage(message: IMessage, filterSettings: Option[FilterSettings]): Unit = {
        if (filterSettings.isDefined)
            capsFilter.filterMessage(message, filterSettings.get)
    }

    @EventSubscriber
    def onCommandExecutionEvent(event: CommandExecutionEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
        if (commandOpt.isDefined)
            commandOpt.get.execute(event)
    }

}
