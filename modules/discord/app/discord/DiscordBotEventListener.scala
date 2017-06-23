package discord

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.models.FilterSettings
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.filters.DiscordCapsFilter
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.MessagesApi
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

import scala.collection.JavaConverters._

class DiscordBotEventListener @Inject()(implicit configuration: Configuration, discordBot: Provider[DiscordBot], messages: MessagesApi, discordCommandRegistry: DiscordCommandRegistry, capsFilter: DiscordCapsFilter, database: Database) {

    val commandPrefix: String = configuration.getString("mtrBot.commandPrefix").getOrElse("!")

    val guildId: String = configuration.getString("mtrBot.guildId").get

    val moderatorRoleId: String = configuration.getString("mtrBot.moderatorRoleId").get

    @EventSubscriber
    def onReadyEvent(event: ReadyEvent): Unit = {
        DiscordLogger.info("Monster Truck Bot ready")
        discordBot.get().client.changeUsername(configuration.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot"))
        discordBot.get().client.changePlayingText(configuration.getString("mtrBot.discord.game").getOrElse("Hap! Hap! Hap!"))
    }

    @EventSubscriber
    def onMessageReceivedEvent(event: MessageReceivedEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        if (!message.getAuthor.isBot && message.getGuild.getStringID == guildId)
            if (message.getContent.startsWith(commandPrefix)) {
                val contentSplit: Array[String] = message.getContent.split("\\s")
                discordBot.get().client.getDispatcher.dispatch(new CommandExecutionEvent(
                    message,
                    contentSplit.head.substring(commandPrefix.length),
                    contentSplit.tail,
                    message.getAuthor
                ))
            } else {
                val filterSettings: Option[FilterSettings] = FilterSettings.get(message.getGuild.getLongID)
                if (message.getAuthor.getLongID != message.getGuild.getOwnerLongID
                    && !message.getAuthor.getRolesForGuild(message.getGuild).asScala.map(_.getStringID).contains(moderatorRoleId)) // See if the mods want to be able to filter themselves
                    filterMessage(message, filterSettings)
            }
    }

    def filterMessage(message: IMessage, filterSettings: Option[FilterSettings]): Unit = {
        if (filterSettings.isDefined && filterSettings.get.capsFilterEnabled)
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
