package discord.filters

import javax.inject.Inject

import com.cobble.bot.common.models.FilterSettings
import discord.util.DiscordMessageUtil
import sx.blah.discord.handle.obj.IMessage

class DiscordLinksFilter @Inject()(discordMessageUtil: DiscordMessageUtil) {

    def filterMessage(message: IMessage, filterSettings: FilterSettings): Unit = {
        if ("(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)".r.findFirstIn(message.getContent).isDefined) {
            message.delete()
            discordMessageUtil.replyToMessage(message, "bot.filter.noLinks")
        }
    }

}
