package common.dao

import anorm.{Macro, RowParser, SqlQuery}
import common.api.{MTRModelAccessor, Model}
import common.models.BotInstance
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database

class BotInstanceDAO @Inject()(db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) extends MTRModelAccessor[BotInstance](db, cache, configRef) {

    override val tableName: String = "bot_instances"

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val parser: RowParser[BotInstance] = Macro.parser[BotInstance]("guild_id")
}
