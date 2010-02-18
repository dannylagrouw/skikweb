package net.skik.model

import java.sql.Connection
import net.skik.util.LangUtils._
import net.skik.util.ReflectionUtils._

case class Composition[A](val property: String, val compositionClass: Class[A], val mapping: Map[String, String]) {
  def composes(propertyName: String) = hasProperty(compositionClass, propertyName) || mapping.contains(propertyName)
  def propertyNameFor(columnName: String) = mapping.get(columnName) match {
    case Some(propertyName) => propertyName
    case None => columnName
  }
}

abstract class BaseObject[T <: Base[T]](implicit modelType: Manifest[T]) {

  //TODO infer tablename from class name
  val tableName: String
  val primaryKey = "id"
  var compositions = List.empty[Composition[_]]
  private val modelClass = modelType.erasure.asInstanceOf[Class[T]]

  def composedOf(attr: Symbol, className: Class[_] = classOf[Nothing], mapping: List[(Symbol, Symbol)] = List.empty[(Symbol, Symbol)]) {
    compositions ::= Composition(attr.name,
        (if (className == classOf[Nothing]) Class.forName(modelClass.getPackage.getName + "." + attr.name.capitalize) else className), 
        Map(mapping.map(t => (t._1.name, t._2.name)).toArray:_*))
  }
  
  def findCompositionFor[A](propertyName: String): Option[Composition[A]] =
    compositions.find(_.composes(propertyName)).asInstanceOf[Option[Composition[A]]]
  
  def find(id: Long): T = Base.find(id, tableName, modelClass)
  
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
  
  def findBySql(sqlQuery: String, args: Map[Symbol, Any]): List[T] =
    find('all, Sql(sqlQuery, args))
  
  def findBySql(sqlQuery: String, args: Any*): List[T] =
    find('all, Sql(sqlQuery, args:_*))
  
  def findFirst(clauses: QueryClause*): Option[T] = find('first, clauses:_*) match {
    case x :: xs => Some(x)
    case _ => None
  }

  def findAll(clauses: QueryClause*): List[T] = find('all, clauses:_*)

  def count(column: Symbol, clauses: QueryClause*): Number =
    calculate('count, column, clauses:_*)
  def count: Long = count()
  def count(clauses: QueryClause*): Long =
    find('first, new ValueMapper[Long], Select("count(*)") :: clauses.toList).head
  def countGrouped(column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    calculateGrouped('count, column, clauses:_*)

  def average(column: Symbol, clauses: QueryClause*): Number =
    calculate('avg, column, clauses:_*)
  def averageGrouped(column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    calculateGrouped('avg, column, clauses:_*)
  def minimum(column: Symbol, clauses: QueryClause*): Number =
    calculate('min, column, clauses:_*)
  def minimumGrouped(column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    calculateGrouped('min, column, clauses:_*)
  def maximum(column: Symbol, clauses: QueryClause*): Number =
    calculate('max, column, clauses:_*)
  def maximumGrouped(column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    calculateGrouped('max, column, clauses:_*)
  def sum(column: Symbol, clauses: QueryClause*): Number =
    calculate('sum, column, clauses:_*)
  def sumGrouped(column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    calculateGrouped('sum, column, clauses:_*)

  //TODO should dbfun be property of Query for better handling of distinct?
  def calculate(function: Symbol, column: Symbol, clauses: QueryClause*): Number =
    find('first,
        new ValueMapper[Number],
        Select(function.name + "(" +
            (if (clauses.exists(_.isInstanceOf[Distinct])) "distinct " else "") +
            column.name + ")") :: clauses.toList).head

  def calculateGrouped(function: Symbol, column: Symbol, clauses: QueryClause*): List[(Any, Number)] =
    clauses.find(_.isInstanceOf[Group]) match {
      case Some(group: Group) => find('all,
        new GroupedValueMapper[(Any, Number)], 
        Select(group.clause.get + ", " + function.name + "(" + column.name + ")") :: clauses.toList)
      case _ => error("Group clause required for calculateGrouped")
  }

  def findOrInitialize(by: By): T = findFirst(by) match {
    case Some(t) => t
    case None => newFrom(Map(by.args.map(_.toTuple):_*))
  }
  
  def findOrCreate(by: By): T = findFirst(by) match {
    case Some(t) => t
    case None => create_!(Map(by.args.map(_.toTuple):_*))
  }
  
  def find(mode: Symbol, clauses: QueryClause*): List[T] =
    find(mode, ClassMapper[T](getClass.getName), clauses)

  private def find[U](mode: Symbol, mapper: Mapper[U], clauses: Seq[QueryClause]): List[U] = {
    execFindQuery(mode, mapper) {q: Query =>
      clauses.foreach {clause => clause match {
        case c: By => println("MATCH by = " + c); q.by = c
        case c: Conditions => println("MATCH conditions = " + c); q.conditions = c
        case c: Order => q.order = c
        case c: Group => q.group = c
        case c: Having => q.having = c
        case c: Limit => q.limit = c
        case c: Offset => q.offset = c
        case c: Select => q.select = c
        case c: Readonly => q.readonly = c
        case c: Distinct => q.distinct = c
        case c: Sql => q.sql = Some(c)
      }}
    }
  }
  
  def execFindQuery[U](mode: Symbol, mapper: Mapper[U])(f: Query => _): List[U] = {
    val query = Base.adapter.createQuery(QueryMode(mode)).table(tableName)
    f(query)
    Base.execQuery(query, mapper)
  }
  
  def newFrom(values: Map[Symbol, Any]): T = {
    val t = modelClass.newInstance
    values.foreach(kv => setProperty(t, kv._1.name, kv._2.asInstanceOf[Object]))
    t
  }
  
  def newFrom(valueMaps: List[Map[Symbol, Any]]): List[T] = valueMaps.map(newFrom)

  def create_!(values: Map[Symbol, Any]): T = {
    val t = newFrom(values)
    t.save_!
    t
  }
  
  def create(values: Map[Symbol, Any]): Option[T] = {
    val t = newFrom(values)
    if (t.save)
      Some(t)
    else
      None
  }
  
  def create(valueMaps: List[Map[Symbol, Any]]): List[T] = valueMaps.map(create_!)
  
  def update(id: Int, args: (Symbol, Any)*): T = {
    Base.updateAttributes(id, tableName, args:_*)
    Base.find(id, tableName, getClass.asInstanceOf[Class[T]])
  }
  def updateAll(fields: String, where: String) =
    Base.adapter.updateAll(Base.connection, tableName, fields, where)

  def delete(id: Int) = {
    Base.adapter.delete(Base.connection, tableName, id)
  }
  def delete(ids: List[Int]) = {
    Base.adapter.delete(Base.connection, tableName, ids)
  }
  
  def destroyAll(conditions: Conditions): Unit =
    findAll(conditions).foreach(_.destroy)

}

abstract class Base[T <: Base[T]](implicit modelType: Manifest[T]) {
  
  val tableName: String = objectProperty(modelClass, "tableName")
  val primaryKey: String = objectProperty(modelClass, "primaryKey")
  var readonly = false
  var frozen = false

  private def modelClass = modelType.erasure.asInstanceOf[Class[T]]
  private def thisId: Int = property(this, primaryKey)
  private def isNew = thisId == 0
  private def freeze = (frozen = true)

  def reload: T = {
    Base.find(thisId, tableName, modelClass)
  }

  def save_! = {
    if (isNew)
      Base.adapter.saveNew(Base.connection, this, tableName)
    else
      Base.adapter.saveExisting(Base.connection, this, tableName)
    true
  }
  
  def save: Boolean = catchToBoolean(save_!)
  
  def updateAttribute(column: Symbol, value: Any): Boolean =
    updateAttributes(column -> value)
  def updateAttributes(args: (Symbol, Any)*): Boolean =
    Base.updateAttributes(thisId, tableName, args:_*)

  def destroy: Unit = {
    Base.adapter.delete(Base.connection, tableName, thisId)
    freeze
  }

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

  def find[T](id: Long, tableName: String, modelClass: Class[T]): T = {
    println("BASE " + modelClass.getName)
    val query = Base.adapter.createQuery(QueryMode.FindFirst).table(tableName)
    query.conditions = Conditions("id = ?", id)
    withDo(Base.adapter.execute(Base.connection, query, ClassMapper(modelClass.getName))) { result =>
      if (result.isEmpty)
        throw (new RecordNotFound(tableName + " id " + id))
      else
        result.first
    }
  }
  
  def updateAttributes(id: Int, tableName: String, args: (Symbol, Any)*): Boolean = catchToBoolean {
    Base.adapter.update(Base.connection, tableName, id, args.map(kv => (kv._1.name, kv._2)):_*)
  }
  
}
