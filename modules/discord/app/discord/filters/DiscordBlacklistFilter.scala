package discord.filters

import javax.inject.Inject

import com.cobble.bot.common.models.FilterSettings
import discord.util.DiscordMessageUtil
import sx.blah.discord.handle.obj.IMessage

class DiscordBlacklistFilter @Inject()(discordMessageUtil: DiscordMessageUtil) {

    def filterMessage(message: IMessage, filterSettings: FilterSettings): Unit = {
        if (filterSettings.blacklistFilterWords.split("\\r\\n").exists(word => word.r.findFirstIn(message.getContent).isDefined)) {
            message.delete()
            discordMessageUtil.replyToMessage(message, "bot.filter.blacklistedWord")
        }
    }

}
