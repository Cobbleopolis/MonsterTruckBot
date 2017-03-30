package com.cobble.bot.common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser}
import com.cobble.bot.common.api.{Model, ModelAccessor}

case class User(discordId: String, username: String, avatarUrl: String, accessToken: String, tokenType: String, refreshToken: String) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'discord_id -> discordId,
        'username -> username,
        'avatar_url -> avatarUrl,
        'access_token -> accessToken,
        'token_type -> tokenType,
        'refresh_token -> refreshToken
    )
}

object User extends ModelAccessor[User, String] {

    override val tableName: String = "users"

    override val idSymbol: Symbol = 'discord_id

    override val insertQuery: String = s"INSERT INTO $tableName (discord_id, username, avatar_url, access_token, token_type, refresh_token) VALUES ({discord_id}, {username}, {avatar_url}, {access_token}, {token_type}, {refresh_token})"

    override val parser: RowParser[User] = Macro.parser[User]("discord_id", "username", "avatar_url", "access_token", "token_type", "refresh_token")

    override val insertParser: RowParser[String] = SqlParser.scalar[String]

}
