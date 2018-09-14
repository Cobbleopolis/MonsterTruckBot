package common.models

import anorm.NamedParameter
import common.api.Model

case class TwitchRegular(guildId: Long, twitchUsername: String) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId, 'twitch_username -> twitchUsername)

}
