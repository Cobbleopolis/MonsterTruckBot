package discord.api

import com.cobble.bot.common.models.FilterSettings
import sx.blah.discord.handle.obj.IMessage

trait DiscordFilter {

    def doesMessageMatchFilter(str: String, settings: FilterSettings): Boolean

    def filterMessage(message: IMessage, filterSettings: FilterSettings): Boolean = {
        val matchFilter: Boolean = doesMessageMatchFilter(message.getContent, filterSettings)
        if(matchFilter)
            onMessageFilter(message)
        matchFilter
    }

    def onMessageFilter(message: IMessage): Unit

}
