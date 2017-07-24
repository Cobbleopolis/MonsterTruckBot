package discord.filters

import javax.inject.Inject

import com.cobble.bot.common.api.filters.BlacklistFilter
import com.cobble.bot.common.models.FilterSettings
import discord.api.DiscordFilter
import discord.util.DiscordMessageUtil
import sx.blah.discord.handle.obj.IMessage

class DiscordBlacklistFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends DiscordFilter with BlacklistFilter {

    override def filterMessage(message: IMessage, filterSettings: FilterSettings): Unit = {
        if (doesMessageMatchFilter(message.getContent, filterSettings)) {
            message.delete()
            discordMessageUtil.replyToMessage(message, "bot.filter.blacklistedWord.message")
        }
    }

}
