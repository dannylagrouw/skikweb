package net.skik.model

import java.sql.Connection
import net.skik.util.LangUtils._

abstract class Base[T] {

  val tableName: String
  val primaryKey = "id"
  
  def find(id: Long): Option[T] = {
    val query = Base.adapter.createQuery(QueryMode.FindFirst).table(tableName)
    query.conditions = Conditions("id = ?", id)
    withDo(Base.adapter.execute(Base.connection, query, ClassMapper(getClass.getName))) { result =>
      if (result.isEmpty) None else Some(result.first)
    }
  }

  def find(mode: Symbol, clauses: QueryClause*) = {
    execFindQuery(mode) {q: Query =>
      clauses.foreach {clause => clause match {
        case c: Conditions => q.conditions = c
        case c: Order => q.order = c
        case c: Limit => q.limit = c
        case c: Offset => q.offset = c
      }}
    }
  }

  def execFindQuery(mode: Symbol)(f: Query => _): List[T] = {
    val query = Base.adapter.createQuery(QueryMode(mode)).table(tableName)
    f(query)
    Base.execQuery(query, ClassMapper[T](getClass.getName))
  }
  
  def save: Unit = {
    Base.adapter.save(Base.connection, this, tableName)
  }
}

object Base {

  var adapter: Adapter = _
  var connection: Connection = _
  
  def establishConnection(adapter: Adapter, host: String, database: String, username: String, password: String): Unit = {
    this.adapter = adapter
    connection = adapter.establishConnection(host, database, username, password)
  }
  
  def execQuery[T](query: Query, mapper: Mapper[T]): List[T] = {
    adapter.execute(connection, query, mapper)
  }

  def execQuery(query: Query): Int = {
    adapter.execute(connection, query)
  }

}
