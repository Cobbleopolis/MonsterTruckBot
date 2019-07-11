package discord.filters

import ackcord.data.Message
import ackcord.syntax._
import common.api.filters.LinksFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import javax.inject.Inject

class DiscordLinksFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with LinksFilter {

    override def onMessageFilter(message: Message): Unit = {
        message.delete()
//        discordMessageUtil.replyToMessage(message, "bot.filter.noLinks.message")
    }

}
