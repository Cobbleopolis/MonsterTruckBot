package discord.api

import com.cobble.bot.common.models.FilterSettings
import sx.blah.discord.handle.obj.IMessage

trait DiscordFilter {

    def filterMessage(message: IMessage, filterSettings: FilterSettings): Unit

}
