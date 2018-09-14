package common.dao

import anorm.{Macro, NamedParameter, RowParser, SQL, SqlParser, SqlQuery}
import common.api.MTRModelAccessor
import common.models.CustomCommand
import common.ref.MtrConfigRef
import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.db.Database

class CustomCommandDAO @Inject()(db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) extends MTRModelAccessor[CustomCommand](db, cache, configRef) {

    override lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")
    override lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND command_name = {command_name}")
    override val tableName: String = "custom_commands"
    override val idSymbol: Symbol = 'guild_id
    override val parser: RowParser[CustomCommand] = Macro.parser[CustomCommand]("guild_id", "command_name", "permission_level", "command_content")
    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
    val getByGuildIdQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} ORDER BY command_name ASC")

    def getCacheId(id: Long, name: String): String = tableName + getCacheIdSuffix(id, name)

    def getCacheIdSuffix(id: Long, name: String): String = s"${super.getCacheIdSuffix(id)}.$name"

    def get(id: Long, name: String): Option[CustomCommand] = {
        cache.getOrElseUpdate(s"$tableName.${getCacheIdSuffix(id)}.$name", configRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getQuery.on(idSymbol -> id, 'command_name -> name).as(parser.singleOpt)
            })
        }
    }

    def getByGuildId(id: Long): List[CustomCommand] = {
        cache.getOrElseUpdate(s"$tableName.${getCacheIdSuffix(id)}", configRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getByGuildIdQuery.on(idSymbol -> id).as(parser.*)
            })
        }
    }

    def insert(customCommand: CustomCommand): Unit = {
        cache.remove(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}")
        cache.set(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}.${customCommand.commandName}", customCommand, configRef.cacheTimeout)
        insert(customCommand.namedParameters: _*)
    }

    def update(customCommand: CustomCommand): Int = {
        cache.remove(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}")
        cache.set(s"$tableName.${getCacheIdSuffix(customCommand.guildId)}.${customCommand.commandName}", customCommand, configRef.cacheTimeout)
        update(customCommand.guildId, customCommand.commandName, customCommand.namedParameters: _*)
    }

    def update(guildId: Long, name: String, params: NamedParameter*): Int = {
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

    def delete(guildId: Long, name: String): Int = {
        cache.remove(s"$tableName.${getCacheIdSuffix(guildId)}")
        cache.remove(s"$tableName.${getCacheIdSuffix(guildId)}.$name")
        db.withConnection(implicit conn => {
            deleteQuery.on(idSymbol -> guildId, 'command_name -> name).executeUpdate()
        })
    }
}
