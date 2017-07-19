package data

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.{BotInstance, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.Configuration
import play.api.cache.SyncCacheApi
import play.api.db.Database

@Singleton
class DatabaseCreation @Inject()(implicit db: Database, conf: Configuration, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef) {

    val botInstanceOpt: Option[BotInstance] = BotInstance.get(mtrConfigRef.guildId)
    if (botInstanceOpt.isEmpty)
        BotInstance.insert(mtrConfigRef.guildId, BotInstance(mtrConfigRef.guildId))

    val filterSettingsOpt: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
    if (filterSettingsOpt.isEmpty)
        FilterSettings.insert(mtrConfigRef.guildId, FilterSettings(mtrConfigRef.guildId))

}
