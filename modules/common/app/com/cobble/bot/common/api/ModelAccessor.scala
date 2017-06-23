package com.cobble.bot.common.api

import anorm._
import play.api.db.Database

trait ModelAccessor[T <: Model, A] {

    lazy val getQuery: SqlQuery = SQL(s"SELECT * FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}}")
    lazy val getAllQuery: SqlQuery = SQL(s"SELECT * FROM $tableName")
    lazy val deleteQuery: SqlQuery = SQL(s"DELETE FROM $tableName WHERE ${idSymbol.name} = {${idSymbol.name}}")
    val tableName: String
    val idSymbol: Symbol
    val getByQueryList: Map[Class[_ <: Model], SqlQuery] = Map()
    val insertQuery: String
    val parser: RowParser[T]

    val insertParser: RowParser[A]


    def get(id: Long)(implicit db: Database): Option[T] = {
        db.withConnection(implicit conn => {
            getQuery.on(idSymbol -> id).as(parser.singleOpt)
        })
    }

    def getAll(implicit db: Database): List[T] = {
        db.withConnection(implicit conn => {
            getAllQuery.as(parser.*)
        })
    }

    def getBy(byType: Class[_ <: Model], params: NamedParameter*)(implicit db: Database): List[T] = {
        val query: Option[SqlQuery] = getByQueryList.get(byType)
        query match {
            case Some(q) =>
                db.withConnection(implicit conn => {
                    q.on(params: _*).as(parser.*)
                })
            case None => throw new IllegalArgumentException("byType is not defined in the getByQueryList")
        }
    }


    def insert(model: T)(implicit db: Database): Unit = {
        db.withConnection(implicit conn => {
            SQL(insertQuery).on(model.namedParameters: _*).executeInsert(insertParser.singleOpt)
        })
    }

    def insert(params: NamedParameter*)(implicit db: Database): Unit = {
        db.withConnection(implicit conn => {
            SQL(insertQuery).on(params: _*).executeInsert(insertParser.singleOpt)
        })
    }

    def insertBatch(models: Seq[T])(implicit db: Database): Unit = {
        if (models.nonEmpty)
            insertBatchParams(models.map(_.namedParameters))
    }

    def insertBatchParams(params: Seq[Seq[NamedParameter]])(implicit db: Database): Unit = {
        val p: Seq[Seq[NamedParameter]] = params.filter(m => m.forall(p => p.value.show != ""))
        try {
            if (p.nonEmpty)
                db.withConnection(implicit conn => {
                    if (p.length == 1)
                        BatchSql(insertQuery, p.head).execute()
                    else
                        BatchSql(insertQuery, p.head, p.tail.flatten).execute()
                })
        } catch {
            case t: Throwable =>
                println(p)
                throw t
        }
    }

    def update(id: Long, params: NamedParameter*)(implicit db: Database): Int = {
        if (params.nonEmpty) {
            val idParam: NamedParameter = idSymbol -> id
            db.withConnection(implicit conn => {
                SQL(s"UPDATE $tableName SET ${params.filterNot(_.name == idSymbol.name).map(p => s"${p.name} = {${p.name}}").mkString(", ")} WHERE ${idSymbol.name} = {${idSymbol.name}}").on(params :+ idParam: _*).executeUpdate()
            })
        } else
            0
    }

    def delete(id: Long)(implicit db: Database): Int = {
        db.withConnection(implicit conn => {
            deleteQuery.on(idSymbol -> id).executeUpdate()
        })
    }
}