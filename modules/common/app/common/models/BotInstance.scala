package common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser, SqlQuery}
import common.api.{Model, ModelAccessor}

case class BotInstance(guildId: Long) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId)
}

object BotInstance extends ModelAccessor[BotInstance, Long] {

    override val tableName: String = "bot_instances"

    override val idSymbol: Symbol = 'guild_id

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id) VALUES ({guild_id})"

    override val parser: RowParser[BotInstance] = Macro.parser[BotInstance]("guild_id")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
}
