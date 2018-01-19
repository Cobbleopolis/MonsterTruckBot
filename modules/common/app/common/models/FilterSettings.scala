package common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser, SqlQuery}
import common.api.PermissionLevel.PermissionLevel
import common.api.{Model, ModelAccessor, PermissionLevel}

case class FilterSettings(
                             guildId: Long,
                             capsFilterEnabled: Boolean = false,
                             capsFilterExemptionLevel: Int = PermissionLevel.MODERATORS.id,
                             capsFilterThreshold: Int = 20,
                             linksFilterEnabled: Boolean = false,
                             linksFilterExemptionLevel: Int = PermissionLevel.MODERATORS.id,
                             blacklistFilterEnabled: Boolean = false,
                             blacklistFilterExemptionLevel: Int = PermissionLevel.MODERATORS.id,
                             blacklistFilterWords: String = ""
                         ) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'caps_filter_enabled -> capsFilterEnabled,
        'caps_filter_exemption_level -> capsFilterExemptionLevel,
        'caps_filter_threshold -> capsFilterThreshold,
        'links_filter_enabled -> linksFilterEnabled,
        'links_filter_exemption_level -> linksFilterExemptionLevel,
        'blacklist_filter_enabled -> blacklistFilterEnabled,
        'blacklist_filter_exemption_level -> blacklistFilterExemptionLevel,
        'blacklist_filter_words -> blacklistFilterWords
    )

    val getCapsFilterExemptionLevel: PermissionLevel = PermissionLevel(capsFilterExemptionLevel)

    val getLinksFilterExemptionLevel: PermissionLevel = PermissionLevel(linksFilterExemptionLevel)

    val getBlackListFilterExemptionLevel: PermissionLevel = PermissionLevel(blacklistFilterExemptionLevel)

}

object FilterSettings extends ModelAccessor[FilterSettings, Long] {

    override val tableName: String = "filter_settings"

    override val idSymbol: Symbol = 'guild_id

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id, caps_filter_enabled, caps_filter_exemption_level, caps_filter_threshold, links_filter_enabled, links_filter_exemption_level, blacklist_filter_enabled, blacklist_filter_exemption_level, blacklist_filter_words) VALUES ({guild_id}, {caps_filter_enabled}, {caps_filter_exemption_level}, {caps_filter_threshold}, {links_filter_enabled}, {links_filter_exemption_level}, {blacklist_filter_enabled}, {blacklist_filter_exemption_level}, {blacklist_filter_words})"

    override val parser: RowParser[FilterSettings] = Macro.parser[FilterSettings]("guild_id", "caps_filter_enabled", "caps_filter_exemption_level", "caps_filter_threshold", "links_filter_enabled", "links_filter_exemption_level", "blacklist_filter_enabled", "blacklist_filter_exemption_level", "blacklist_filter_words")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
}
