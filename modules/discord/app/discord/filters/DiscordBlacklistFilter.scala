package discord.filters

import common.api.filters.BlacklistFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject

class DiscordBlacklistFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with BlacklistFilter {

    override def onMessageFilter(message: Message): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.blacklistedWord.message")
    }

}
