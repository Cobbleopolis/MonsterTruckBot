package discord.filters

import javax.inject.Inject

import common.api.filters.LinksFilter
import common.models.FilterSettings
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import sx.blah.discord.handle.obj.IMessage

class DiscordLinksFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with LinksFilter {

    override def onMessageFilter(message: IMessage): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.noLinks.message")
    }

}
