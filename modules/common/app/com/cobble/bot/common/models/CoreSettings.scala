package com.cobble.bot.common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser, SqlQuery}
import com.cobble.bot.common.api.{Model, ModelAccessor}

case class CoreSettings(guildId: Long, moderatorRoleId: Option[String] = None) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId, 'moderator_role_id -> moderatorRoleId)

}

object CoreSettings extends ModelAccessor[CoreSettings, Long] {

    override val tableName: String = "core_settings"

    override val idSymbol: Symbol = 'guild_id

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id, moderator_role_id) VALUES ({guild_id}, {moderator_role_id})"

    override val parser: RowParser[CoreSettings] = Macro.parser[CoreSettings]("guild_id", "moderator_role_id")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]

}
