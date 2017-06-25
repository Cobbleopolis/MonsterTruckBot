package com.cobble.bot.common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser, SqlQuery}
import com.cobble.bot.common.api.{Model, ModelAccessor}

case class FilterSettings(guildId: Long, capsFilterEnabled: Boolean = false, capsFilterThreshold: Int = 20, linksFilterEnabled: Boolean = false) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'caps_filter_enabled -> capsFilterEnabled,
        'caps_filter_threshold -> capsFilterThreshold,
        'links_filter_enabled -> linksFilterEnabled
    )

}

object FilterSettings extends ModelAccessor[FilterSettings, Long] {

    override val tableName: String = "filter_settings"

    override val idSymbol: Symbol = 'guild_id

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id, caps_filter_enabled, caps_filter_threshold, links_filter_enabled) VALUES ({guild_id}, {caps_filter_enabled}, {caps_filter_threshold}, {links_filter_enabled})"

    override val parser: RowParser[FilterSettings] = Macro.parser[FilterSettings]("guild_id", "caps_filter_enabled", "caps_filter_threshold", "links_filter_enabled")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
}
