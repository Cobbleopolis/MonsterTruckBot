package discord.filters

import javax.inject.Inject

import com.cobble.bot.common.api.filters.CapsFilter
import com.cobble.bot.common.models.FilterSettings
import discord.util.DiscordMessageUtil
import sx.blah.discord.handle.obj.IMessage

class DiscordCapsFilter @Inject()(discordMessageUtil: DiscordMessageUtil) extends CapsFilter {

    def filterMessage(message: IMessage, filterSettings: FilterSettings): Unit = {
        if(doesMessageMatchFilter(message.getContent, filterSettings)) {
            message.delete()
            discordMessageUtil.replyToMessage(message, "bot.filter.noCaps")
        }
    }

}
