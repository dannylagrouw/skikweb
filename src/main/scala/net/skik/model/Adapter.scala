package net.skik.model

import java.sql.Connection
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

abstract class Adapter {

  def establishConnection(host: String, database: String, username: String, password: String): Connection

  def createQuery(mode: QueryMode.Value): Query
 
  def execute(conn: Connection, query: Query): Int = {
    val stmt = conn.prepareStatement(query.toSql)
    var result = List.empty[Any]
    using(stmt) { stmt =>
      var i = 0
      query.whereArgs.foreach { a =>
        i += 1
        stmt.setObject(i, a)
      }
      stmt.executeUpdate
    }
  }
  
  def execute[T](conn: Connection, query: Query, mapper: Mapper[T]): List[T] = {
    println("QUERY = " + query.toSql)
    println("ARGS = " + query.whereArgs.mkString(","))
    val stmt = conn.prepareStatement(query.toSql)
    var result = List.empty[T]
    using(stmt) { stmt =>
      var i = 0
      query.whereArgs.foreach { a =>
        i += 1
        stmt.setObject(i, a)
      }
      if (stmt.execute) {
        mapper.metaData = stmt.getMetaData
        mapper.readonly = query.readonly.readonly
        val rs = stmt.getResultSet
        while (rs.next) {
          val o = mapper.map(rs)
          println("RESULT = " + o)
          result ::= o
        }
      }
    }
    result.reverse
  }
  
  def saveNew[T](conn: Connection, modelObject: T, tableName: String): Unit = {
    // TODO cache this
    // TODO get schema name from config
    // TODO wrap jdbc
    // TODO error handling
    // TODO abstract query, fields
    // TODO handle custom primary key / generated key
    println("SAVE get column info for " + tableName)
    val (id, columns) = getColumnValues(conn, modelObject, tableName)
    val query = "insert into " + tableName + " (" + columns.map(kv => kv._1).mkString(", ") +
        ") values (" + columns.map(kv => asSqlValue(kv._2)).mkString(", ") + ")"
    println("QUERY = " + query)
    val stmt = conn.createStatement
    stmt.execute(query);
    
    val keyRS = stmt.getGeneratedKeys
    if (keyRS.next) {
      PropertyUtils.setSimpleProperty(modelObject, "id", keyRS.getInt(1))
    }
  }
    
  def saveExisting[T](conn: Connection, modelObject: T, tableName: String): Unit = {
    // TODO cache this
    // TODO get schema name from config
    // TODO wrap jdbc
    // TODO error handling
    // TODO abstract query, fields
    // TODO handle custom primary key / generated key
    println("UPDATE get column info for " + tableName)
    val (id, columns) = getColumnValues(conn, modelObject, tableName)
    update(conn, tableName, id, columns.toArray:_*)
  }
  
  def getColumnValues[T](conn: Connection, modelObject: T, tableName: String): (Int, Map[String, Any]) = {
    // TODO let T be T <: (def getKey: Any)???
    var columns = Map.empty[String, Any]
    val dbmd = conn.getMetaData
    val colsRS = dbmd.getColumns(null, "inschr_rer", tableName, null)
    var id: Int = 0
    while (colsRS.next) {
      val columnName = colsRS.getString("COLUMN_NAME")
      if (PropertyUtils.isReadable(modelObject, columnName)) {
        if (columnName == "id")
          id = PropertyUtils.getSimpleProperty(modelObject, columnName).asInstanceOf[Int]
        else
          columns += (columnName -> PropertyUtils.getSimpleProperty(modelObject, columnName))
      }
    }
    (id, columns)
  }
  
  def update(conn: Connection, tableName: String, id: Int, args: (String, Any)*): Unit = {
    val query = "update " + tableName + " set " + 
        args.map(kv => kv._1 + " = " + asSqlValue(kv._2)).mkString(", ") +
        " where id = " + id
    println("QUERY = " + query)
    val stmt = conn.createStatement
    stmt.executeUpdate(query)
  }
  
  def asSqlValue(value: Any) = value match {
    case None => "null"
    case null => "null"
    case Some(v: Number) => v.toString
    case v: Number => v.toString
    case _ => "'" + value + "'"
  }
    
  def updateAll(conn: Connection, tableName: String, fields: String, where: String) = {
    val query = "update " + tableName + " set " + fields + " where " + where
    println("QUERY = " + query)
    val stmt = conn.createStatement
    stmt.executeUpdate(query)
  }
  
  def delete(conn: Connection, tableName: String, id: Int) = {
    val query = "delete from " + tableName + " where id = ?"
    println("QUERY = " + query)
    val stmt = conn.prepareStatement(query)
    using(stmt) { stmt =>
      stmt.setInt(1, id)
      stmt.executeUpdate
    }
  }
  
  def delete(conn: Connection, tableName: String, ids: List[Int]) = {
    val query = "delete from " + tableName + " where id in (" + ids.mkString(",") + ")"
    println("QUERY = " + query)
    val stmt = conn.prepareStatement(query)
    using(stmt) (_.executeUpdate)
  }
  
}

