package common.api

import anorm.{NamedParameter, RowParser, SqlParser}

trait MTRModelAccessor[T <: Model] extends ModelAccessor[T, Long] {

    override val idSymbol: Symbol = 'guild_id

    override def getCacheIdSuffix(id: Long): String = java.lang.Long.toUnsignedString(id)

    def getIdNamedParameter(id: Long): NamedParameter = idSymbol -> id

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]

}
