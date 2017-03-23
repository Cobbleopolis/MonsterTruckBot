package com.cobble.bot.common.models

import anorm.{Macro, NamedParameter, RowParser}
import com.cobble.bot.common.api.{Model, ModelAccessor}

case class User(discordId: String, username: String, oAuthToken: String, refreshToken: String) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('discord_id -> discordId, 'username -> username, 'oauth_token -> oAuthToken, 'refresh_token -> refreshToken)
}

object User extends ModelAccessor[User] {

    override val tableName: String = "users"

    override val idSymbol: Symbol = 'discord_id

    override val insertQuery: String = s"INSERT INTO $tableName (discord_id, username, oauth_token, refresh_token) VALUES ({discord_id}, {username}, {oauth_token}, {refresh_token})"

    override val parser: RowParser[User] = Macro.parser[User]("discord_id", "username", "oauth_token", "refresh_token")

}
