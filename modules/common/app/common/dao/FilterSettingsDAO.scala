package common.dao

import anorm.{Macro, RowParser, SqlParser, SqlQuery}
import common.api.{MTRModelAccessor, Model}
import common.models.FilterSettings
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database

class FilterSettingsDAO @Inject()(db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) extends MTRModelAccessor[FilterSettings](db, cache, configRef) {

    override val tableName: String = "filter_settings"

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val parser: RowParser[FilterSettings] = Macro.parser[FilterSettings]("guild_id", "caps_filter_enabled", "caps_filter_exemption_level", "caps_filter_threshold", "links_filter_enabled", "links_filter_exemption_level", "blacklist_filter_enabled", "blacklist_filter_exemption_level", "blacklist_filter_words")
}