package common.api

import anorm._
import common.ref.MtrConfigRef
import play.api.cache._
import play.api.db.Database

abstract class ModelAccessor[T <: Model, A](db: Database, cache: SyncCacheApi, configRef: MtrConfigRef) {

    lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}}")
    lazy val getAllQuery: SqlQuery = SQL(s"SELECT * FROM $tableName")
    lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}}")
    val tableName: String
    val idSymbol: Symbol
    val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()
//    val insertQuery: String
    val parser: RowParser[T]

    val insertParser: RowParser[A]

    def getCacheIdSuffix(id: A): String = id.toString
    
    def getCacheId(id: A): String = s"$tableName.${getCacheIdSuffix(id)}"

    def getIdNamedParameter(id: A): NamedParameter


    def get(id: A): Option[T] = {
        cache.getOrElseUpdate[Option[T]](getCacheId(id), configRef.cacheTimeout) {
            db.withConnection(implicit conn => {
                getQuery.on(getIdNamedParameter(id)).as(parser.singleOpt)
            })
        }
    }

    def getAll: List[T] = {
        db.withConnection(implicit conn => {
            getAllQuery.as(parser.*)
        })
    }

    def getBy(byType: Class[_ <: Model], params: NamedParameter*): List[T] = {
        val query: Option[SqlQuery] = getByQueryList.get(byType)
        query match {
            case Some(q) =>
                db.withConnection(implicit conn => {
                    q.on(params: _*).as(parser.*)
                })
            case None => throw new IllegalArgumentException("byType is not defined in the getByQueryList")
        }
    }

    def getInsertQuery(params: Seq[NamedParameter]): String = {
        val paramNames = params.map(_.name)
        s"INSERT INTO $tableName (${paramNames.mkString(", ")}) VALUES (${paramNames.mkString("{", "}, {", "}")})"
    }

    def insert(id: A, model: T): Unit = {
        cache.set(getCacheId(id), model, configRef.cacheTimeout)
        insert(model.namedParameters: _*)
    }

    def insert(params: NamedParameter*): Unit = {
        db.withConnection(implicit conn => {
            SQL(getInsertQuery(params)).on(params: _*).executeInsert(insertParser.singleOpt)
        })
    }

    def insertBatch(models: Seq[T]): Unit = {
        if (models.nonEmpty)
            insertBatchParams(models.map(_.namedParameters))
    }

    def insertBatchParams(params: Seq[Seq[NamedParameter]]): Unit = {
        val p: Seq[Seq[NamedParameter]] = params.filter(m => m.forall(p => p.value.show != ""))
        try {
            if (p.nonEmpty) {
                val insertQuery = getInsertQuery(p.head)
                db.withConnection(implicit conn => {
                    if (p.length == 1)
                        BatchSql(insertQuery, p.head).execute()
                    else
                        BatchSql(insertQuery, p.head, p.tail.flatten).execute()
                })
            }
        } catch {
            case t: Throwable =>
                println(p)
                throw t
        }
    }

    def update(id: A, model: T): Int = {
        cache.set(getCacheId(id), model, configRef.cacheTimeout)
        update(id, model.namedParameters: _*)
    }

    def update(id: A, params: NamedParameter*): Int = {
        if (params.nonEmpty) {
            val idParam: NamedParameter = getIdNamedParameter(id)
            db.withConnection(implicit conn => {
                SQL(s"UPDATE $tableName SET ${params.filterNot(_.name == idSymbol.name).map(p => s"${p.name} = {${p.name}}").mkString(", ")} WHERE ${idSymbol.name} = {${idSymbol.name}}").on(params :+ idParam: _*).executeUpdate()
            })
        } else
            0
    }

    def delete(id: A): Int = {
        cache.remove(getCacheId(id))
        db.withConnection(implicit conn => {
            deleteQuery.on(getIdNamedParameter(id)).executeUpdate()
        })
    }
}