package discord.filters

import common.api.filters.LinksFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject

class DiscordLinksFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with LinksFilter {

    override def onMessageFilter(message: Message): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.noLinks.message")
    }

}
