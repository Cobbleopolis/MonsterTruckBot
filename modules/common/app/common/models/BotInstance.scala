package common.models

import anorm.NamedParameter
import common.api.Model

case class BotInstance(guildId: Long) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId)
}
