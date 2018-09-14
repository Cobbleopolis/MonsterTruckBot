package common.models

import anorm._
import common.api.{MTRModelAccessor, Model}
import common.ref.MtrConfigRef
import play.api.cache.SyncCacheApi
import play.api.db.Database

case class TwitchRegular(guildId: Long, twitchUsername: String) extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq('guild_id -> guildId, 'twitch_username -> twitchUsername)

}

object TwitchRegular extends MTRModelAccessor[TwitchRegular] {

    override lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND twitch_username = {twitch_username}")
    override lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} AND twitch_username = {twitch_username}")
    override val tableName: String = "twitch_regulars"
    override val idSymbol: Symbol = 'guild_id
    override val parser: RowParser[TwitchRegular] = Macro.parser[TwitchRegular]("guild_id", "twitch_username")
    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
    val getByGuildIdQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}} ORDER BY twitch_username ASC")

    def getCacheId(id: Long, name: String): String = tableName + getCacheIdSuffix(id, name)

    def getCacheIdSuffix(id: Long, name: String): String = s"${super.getCacheIdSuffix(id)}.$name"

    def get(id: Long, twitchUsername: String)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Option[TwitchRegular] = {
        cache.getOrElseUpdate(getCacheId(id, twitchUsername), mtrConfigRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getQuery.on(idSymbol -> id, 'twitch_username -> twitchUsername).as(parser.singleOpt)
            })
        }
    }

    def getByGuildId(id: Long)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): List[TwitchRegular] = {
        cache.getOrElseUpdate(getCacheId(id), mtrConfigRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getByGuildIdQuery.on(idSymbol -> id).as(parser.*)
            })
        }
    }

    def update(twitchRegular: TwitchRegular)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Int = {
        cache.remove(getCacheId(twitchRegular.guildId))
        cache.set(getCacheId(twitchRegular.guildId, twitchRegular.twitchUsername), twitchRegular, mtrConfigRef.cacheTimeout)
        update(twitchRegular.guildId, twitchRegular.twitchUsername, twitchRegular.namedParameters: _*)
    }

    def update(guildId: Long, twitchUsername: String, params: NamedParameter*)(implicit db: Database): Int = {
        val updatableParams: Seq[NamedParameter] = params.filterNot(p => p.name == idSymbol.name && p.name == "twitch_username")
        if (updatableParams.nonEmpty) {
            val idParam: NamedParameter = idSymbol -> guildId
            val twitchUsernameParam: NamedParameter = 'twitch_username -> twitchUsername
            db.withConnection(implicit conn => {
                SQL(s"UPDATE $tableName SET ${updatableParams.map(p => s"${p.name} = {${p.name}}").mkString(", ")} WHERE ${idSymbol.name} = {${idSymbol.name}} AND ${twitchUsernameParam.name} = {${twitchUsernameParam.name}}").on(params :+ idParam :+ twitchUsernameParam: _*).executeUpdate()
            })
        } else
            0
    }

    def insert(twitchRegular: TwitchRegular)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Unit = {
        cache.remove(getCacheId(twitchRegular.guildId))
        cache.set(getCacheId(twitchRegular.guildId, twitchRegular.twitchUsername), twitchRegular, mtrConfigRef.cacheTimeout)
        insert(twitchRegular.namedParameters: _*)
    }

    def delete(guildId: Long, twitchUsername: String)(implicit db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef): Int = {
        cache.remove(getCacheId(guildId))
        cache.remove(getCacheId(guildId, twitchUsername))
        db.withConnection(implicit conn => {
            deleteQuery.on(idSymbol -> guildId, 'twitch_username -> twitchUsername).executeUpdate()
        })
    }
}
