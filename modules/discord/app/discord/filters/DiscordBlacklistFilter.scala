package discord.filters

import ackcord.data.Message
import common.api.filters.BlacklistFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import javax.inject.Inject
import ackcord.syntax._

class DiscordBlacklistFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with BlacklistFilter {

    override def onMessageFilter(message: Message): Unit = {
        message.delete()
//        discordMessageUtil.replyToMessage(message, "bot.filter.blacklistedWord.message")
    }

}
