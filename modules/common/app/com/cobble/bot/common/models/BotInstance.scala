package com.cobble.bot.common.models

import anorm.{Macro, NamedParameter, RowParser, SqlQuery}
import com.cobble.bot.common.api.{Model, ModelAccessor}

case class BotInstance(guildId: String, twitchAccount: String) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId, 'twitch_account -> twitchAccount)
}

object BotInstance extends ModelAccessor[BotInstance] {

    override val tableName: String = "bot_instances"

    override val idSymbol: Symbol = 'guild_id

    override val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id, twitch_account) VALUES ({guild_id}, {twitch_account})"

    override val parser: RowParser[BotInstance] = Macro.parser[BotInstance]("guild_id", "twitch_account")
}
