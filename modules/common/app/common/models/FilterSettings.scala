package common.models

import anorm.NamedParameter
import common.api.PermissionLevel.PermissionLevel
import common.api.{Model, PermissionLevel}

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
