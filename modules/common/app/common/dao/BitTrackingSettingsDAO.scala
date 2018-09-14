package common.dao

import anorm.{Macro, RowParser}
import common.api.MTRModelAccessor
import common.models.BitTrackingSettings
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database

class BitTrackingSettingsDAO @Inject()(db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) extends MTRModelAccessor[BitTrackingSettings](db, cache, configRef) {

    override val tableName = "bit_tracking_settings"

    override val parser: RowParser[BitTrackingSettings] = Macro.parser[BitTrackingSettings]("guild_id", "current_mode", "bit_game_message", "bits_message", "goal_message")
}
