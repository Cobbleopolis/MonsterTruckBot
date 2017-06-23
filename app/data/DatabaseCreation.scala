package data

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.{BotInstance, FilterSettings}
import play.api.Configuration
import play.api.db.Database

@Singleton
class DatabaseCreation @Inject()(implicit db: Database, conf: Configuration) {

    val guildId: Long = java.lang.Long.parseUnsignedLong(conf.getString("mtrBot.guildId").get)

    val botInstanceOpt: Option[BotInstance] = BotInstance.get(guildId)
    if (botInstanceOpt.isEmpty)
        BotInstance.insert(BotInstance(guildId))

    val filterSettingsOpt: Option[FilterSettings] = FilterSettings.get(guildId)
    if (filterSettingsOpt.isEmpty)
        FilterSettings.insert(FilterSettings(guildId))

}
