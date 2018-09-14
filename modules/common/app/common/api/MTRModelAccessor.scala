package common.api

import anorm.{NamedParameter, RowParser, SqlParser}
import common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

abstract class MTRModelAccessor[T <: Model](db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) extends ModelAccessor[T, Long](db, cache, configRef) {

    override val idSymbol: Symbol = 'guild_id

    override def getCacheIdSuffix(id: Long): String = java.lang.Long.toUnsignedString(id)

    def getIdNamedParameter(id: Long): NamedParameter = idSymbol -> id

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]

}
