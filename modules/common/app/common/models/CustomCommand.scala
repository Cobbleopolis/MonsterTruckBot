package common.models

import anorm._
import common.api.PermissionLevel.PermissionLevel
import common.api.{MTRModelAccessor, Model, PermissionLevel}
import common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

case class CustomCommand(guildId: Long, commandName: String, permissionLevel: Int = PermissionLevel.EVERYONE.id, commandContent: String = "") extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'command_name -> commandName,
        'permission_level -> permissionLevel,
        'command_content -> commandContent
    )

    val getPermissionLevel: PermissionLevel = PermissionLevel(permissionLevel)
}

object CustomCommand extends MTRModelAccessor[CustomCommand] {

    override lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")
    override lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")
    override val tableName: String = "custom_commands"
    override val idSymbol: Symbol = 'guild_id
    override val parser: RowParser[CustomCommand] = Macro.parser[CustomCommand]("guild_id", "command_name", "permission_level", "command_content")
    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
    val getByGuildIdQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} ORDER BY command_name ASC")

    def getCacheId(id: Long, name: String): String = tableName + getCacheIdSuffix(id, name)

    def getCacheIdSuffix(id: Long, name: String): String = s"${super.getCacheIdSuffix(id)}.$name"

    def get(id: Long, name: String)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Option[CustomCommand] = {
        cache.getOrElseUpdate(s"$tableName.${getCacheIdSuffix(id)}.$name", mtrConfigRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getQuery.on(idSymbol -> id, 'command_name -> name).as(parser.singleOpt)
            })
        }
    }

    def getByGuildId(id: Long)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): List[CustomCommand] = {
        cache.getOrElseUpdate(s"$tableName.${getCacheIdSuffix(id)}", mtrConfigRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getByGuildIdQuery.on(idSymbol -> id).as(parser.*)
            })
        }
    }

    def insert(customCommand: CustomCommand)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Unit = {
        cache.remove(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}")
        cache.set(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}.${customCommand.commandName}", customCommand, mtrConfigRef.cacheTimeout)
        insert(customCommand.namedParameters: _*)
    }

    def update(customCommand: CustomCommand)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Int = {
        cache.remove(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}")
        cache.set(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}.${customCommand.commandName}", customCommand, mtrConfigRef.cacheTimeout)
        update(customCommand.guildId, customCommand.commandName, customCommand.namedParameters: _*)
    }

    def update(guildId: Long, name: String, params: NamedParameter*)(implicit db: Database): Int = {
        val updatableParams: Seq[NamedParameter] = params.filterNot(p => p.name == idSymbol.name && p.name == "command_name")
        if (updatableParams.nonEmpty) {
            val idParam: NamedParameter = idSymbol -> guildId
            val nameParam: NamedParameter = 'command_name -> name
            db.withConnection(implicit conn => {
                SQL(s"UPDATE $tableName SET ${updatableParams.map(p => s"${p.name} = {${p.name}}").mkString(", ")} WHERE ${idSymbol.name} = {${idSymbol.name}} AND ${nameParam.name} = {${nameParam.name}}").on(params :+ idParam :+ nameParam: _*).executeUpdate()
            })
        } else
            0
    }

    def delete(guildId: Long, name: String)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Int = {
        cache.remove(s"$tableName.${getCacheIdSuffix(guildId)}")
        cache.remove(s"$tableName.${getCacheIdSuffix(guildId)}.$name")
        db.withConnection(implicit conn => {
            deleteQuery.on(idSymbol -> guildId, 'command_name -> name).executeUpdate()
        })
    }
}

