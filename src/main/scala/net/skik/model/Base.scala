package net.skik.model

import java.sql.Connection
import net.skik.util.LangUtils._
import org.apache.commons.beanutils._

abstract class Base[T <: Base[T]] {

  val tableName: String
  val primaryKey = "id"
  var readonly = false
  
  private def getObjectClass: Class[T] = Class.forName(getClass.getName.dropRight(1)).asInstanceOf[Class[T]]
    
  def find(id: Long): T = {
    val query = Base.adapter.createQuery(QueryMode.FindFirst).table(tableName)
    query.conditions = Conditions("id = ?", id)
    withDo(Base.adapter.execute(Base.connection, query, ClassMapper(getClass.getName))) { result =>
      if (result.isEmpty)
        throw (new RecordNotFound(tableName + " id " + id))
      else
        result.first
    }
  }
  
  def find(ids: Long*): List[T] = {
    val query = Base.adapter.createQuery(QueryMode.FindAll).table(tableName)
    query.conditions = Conditions("id in (" + ids.mkString(",") + ")")
    withDo(Base.adapter.execute(Base.connection, query, ClassMapper(getClass.getName))) { result =>
      if (result.size != ids.size)
        throw (new RecordNotFound(tableName + " ids " + ids))
      else
        result
    }    
  }

  def findFirst(clauses: QueryClause*): T = find('first, clauses:_*).first

  def findAll(clauses: QueryClause*): List[T] = find('all, clauses:_*)

  def find(mode: Symbol, clauses: QueryClause*): List[T] = {
    execFindQuery(mode) {q: Query =>
      clauses.foreach {clause => clause match {
        case c: Conditions => q.conditions = c
        case c: Order => q.order = c
        case c: Group => q.group = c
        case c: Limit => q.limit = c
        case c: Offset => q.offset = c
		case c: Select => q.select = c
		case c: Readonly => q.readonly = c
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
  
  def newFrom(values: Map[Symbol, Any]): T = {
    val t = getObjectClass.newInstance.asInstanceOf[T]
    values.foreach(kv => PropertyUtils.setSimpleProperty(t, kv._1.name, kv._2))
    t
  }
  
  def newFrom(valueMaps: List[Map[Symbol, Any]]): List[T] = valueMaps.map(newFrom)

  def create(values: Map[Symbol, Any]): T = {
    val t = newFrom(values)
    t.save
    t
  }
  
  def create(valueMaps: List[Map[Symbol, Any]]): List[T] = valueMaps.map(create)
}

object Base {

  var adapter: Adapter = _
  var connection: Connection = _

//  
  
//  def getObjectClass[T]: Class[T] = null
//  
//  def apply[T](values: Map[Symbol, Any]): T = {
//    getObjectClass[T].newInstance
//  }
  
//  def apply[T](values: Map[Symbol, Any])(implicit baseClass: Manifest[T]): T = {
//    baseClass.erasure.newInstance.asInstanceOf[T]
//  }

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
