package discord

import javax.inject.{Inject, Provider}

import com.cobble.bot.common.models.FilterSettings
import com.cobble.bot.common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.filters.{DiscordCapsFilter, DiscordLinksFilter}
import play.api.db.Database
import play.api.i18n.MessagesApi
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

import scala.collection.JavaConverters._

class DiscordBotEventListener @Inject()(implicit config: MtrConfigRef, discordBot: Provider[DiscordBot], messages: MessagesApi, discordCommandRegistry: DiscordCommandRegistry, capsFilter: DiscordCapsFilter, linksFilter: DiscordLinksFilter, database: Database) {

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
            if (filterSettings.get.capsFilterEnabled)
                capsFilter.filterMessage(message, filterSettings.get)
            if (filterSettings.get.linksFilterEnabled)
                linksFilter.filterMessage(message, filterSettings.get)

        }
    }

    @EventSubscriber
    def onCommandExecutionEvent(event: CommandExecutionEvent): Unit = {
        implicit val message: IMessage = event.getMessage
        val commandOpt: Option[DiscordCommand] = discordCommandRegistry.commands.get(event.getCommand)
        DiscordLogger.logger.info(s"GOT COMMAND: ${commandOpt.isDefined}")
        if (commandOpt.isDefined)
            commandOpt.get.execute(event)
    }

}
