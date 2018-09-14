package discord.filters

import common.api.filters.BlacklistFilter
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import javax.inject.Inject
import sx.blah.discord.handle.obj.IMessage

class DiscordBlacklistFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with BlacklistFilter {

    override def onMessageFilter(message: IMessage): Unit = {
        message.delete()
        discordMessageUtil.replyToMessage(message, "bot.filter.blacklistedWord.message")
    }

}
