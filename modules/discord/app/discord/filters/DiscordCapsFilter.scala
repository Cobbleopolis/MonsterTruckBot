package discord.filters

import common.api.filters.CapsFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import javax.inject.Inject
import sx.blah.discord.handle.obj.IMessage

class DiscordCapsFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with CapsFilter {

    override def onMessageFilter(message: IMessage): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.noCaps.message")
    }
}
