package discord.filters

import common.api.filters.CapsFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.Message
import javax.inject.Inject

class DiscordCapsFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with CapsFilter {

    override def onMessageFilter(message: Message): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.noCaps.message")
    }
}
