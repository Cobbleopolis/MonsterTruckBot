package com.cobble.bot.common.models

import anorm._
import com.cobble.bot.common.api.{Model, ModelAccessor, PermissionLevel}
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.cache.CacheApi
import play.api.db.Database

case class CustomCommand(guildId: Long, commandName: String, permissionLevel: Int = PermissionLevel.EVERYONE.id, commandContent: String = "") extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId, 'command_name -> commandName, 'permission_level -> permissionLevel, 'command_content -> commandContent)
}

object CustomCommand extends ModelAccessor[CustomCommand, Long] {

    override val tableName: String = "custom_commands"

    override val idSymbol: Symbol = 'guild_id

    override val insertQuery: String = s"INSERT INTO $tableName (guild_id, command_name, permission_level, command_content) VALUES ({guild_id}, {command_name}, {permission_level}, {command_content})"

    override val parser: RowParser[CustomCommand] = Macro.parser[CustomCommand]("guild_id", "command_name", "permission_level", "command_content")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]

    override lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")

    override lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")

    def get(id: Long, name: String)(implicit db: Database, cache: CacheApi, mtrConfigRef: MtrConfigRef): Option[CustomCommand] = {
        cache.getOrElse(s"$tableName.${java.lang.Long.toUnsignedString(id)}.$name", mtrConfigRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getQuery.on(idSymbol -> id, 'command_name -> name).as(parser.singleOpt)
            })
        }
    }

    def insert(customCommand: CustomCommand)(implicit db: Database, cache: CacheApi, mtrConfigRef: MtrConfigRef): Unit = {
        cache.set(s"$tableName.${java.lang.Long.toUnsignedString(customCommand.guildId)}.${customCommand.commandName}", customCommand, mtrConfigRef.cacheTimeout)
        insert(customCommand.namedParameters: _*)
    }

    def update(customCommand: CustomCommand)(implicit db: Database, cache: CacheApi, mtrConfigRef: MtrConfigRef): Int = {
        cache.set(s"$tableName.${java.lang.Long.toUnsignedString(customCommand.guildId)}.${customCommand.commandName}", customCommand, mtrConfigRef.cacheTimeout)
        update(customCommand.guildId, customCommand.commandName, customCommand.namedParameters: _*)
    }

    def update(guildId: Long, name: String, params: NamedParameter*)(implicit db: Database): Int = {
        if (params.nonEmpty) {
            val idParam: NamedParameter = idSymbol -> guildId
            val nameParam: NamedParameter = 'command_name -> name
            db.withConnection(implicit conn => {
                SQL(s"UPDATE $tableName SET ${params.filterNot(p => p.name == idSymbol.name && p.name == "command_name").map(p => s"${p.name} = {${p.name}}").mkString(", ")} WHERE ${idSymbol.name} = {${idSymbol.name}} AND ${nameParam.name} = {${nameParam.name}}").on(params :+ idParam :+ nameParam: _*).executeUpdate()
            })
        } else
            0
    }

    def delete(guildId: Long, name: String)(implicit db: Database, cache: CacheApi): Int = {
        cache.remove(s"$tableName.${java.lang.Long.toUnsignedString(guildId)}.$name")
        db.withConnection(implicit conn => {
            deleteQuery.on(idSymbol -> guildId, 'command_name -> name).executeUpdate()
        })
    }
}

