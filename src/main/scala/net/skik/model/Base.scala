package net.skik.model

import java.sql.Connection
import net.skik.util.LangUtils._

trait Base {

  val tableName: String
  val primaryKey = "id"
  
  def find(id: Long): Option[Any] = {
    val query = Base.adapter.createQuery(QueryMode.FindFirst).table(tableName)
    query.conditions = Conditions("id = ?", id)
    val className = withDo(getClass.getName) { name =>
      if (name.takeRight(1) == "$") name.dropRight(1) else name
    }
    withDo(Base.adapter.execute(Base.connection, query, ClassMapper(className))) { result =>
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
/*  
  def find(mode: Symbol, conditions: Conditions = Conditions.empty): List[Any] = {
    execFindQuery(mode) {q: Query => q.where(conditions)}
  }
  
  def find(mode: Symbol, order: Option[String]): List[Any] = {
    execFindQuery(mode) {q: Query => q.order(order)}
  }
  
  def find(mode: Symbol, conditions: Conditions, order: Option[String]): List[Any] = {
    execFindQuery(mode) {q: Query => q.where(conditions).order(order)}
  }
*/
  def execFindQuery(mode: Symbol)(f: Query => _): List[Any] = {
    val query = Base.adapter.createQuery(QueryMode(mode)).table(tableName)
    f(query)
    Base.execQuery(query, ClassMapper(getClass))
  }
}

object Base {

  var adapter: Adapter = _
  var connection: Connection = _
  
  def establishConnection(adapter: Adapter, host: String, database: String, username: String, password: String): Unit = {
    this.adapter = adapter
    connection = adapter.establishConnection(host, database, username, password)
  }
  
  def execQuery(query: Query, mapper: Mapper): List[Any] = {
    adapter.execute(connection, query, mapper)
  }

  def execQuery(query: Query): Int = {
    adapter.execute(connection, query)
  }

}
