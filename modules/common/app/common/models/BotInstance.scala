package common.models

import anorm.{Macro, NamedParameter, RowParser, SqlQuery}
import common.api.{MTRModelAccessor, Model}

case class BotInstance(guildId: Long) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId)
}

object BotInstance extends MTRModelAccessor[BotInstance] {

    override val tableName: String = "bot_instances"

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val parser: RowParser[BotInstance] = Macro.parser[BotInstance]("guild_id")
}
