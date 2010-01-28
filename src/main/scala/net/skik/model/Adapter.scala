package net.skik.model

import java.sql.Connection
import org.apache.commons.beanutils._

abstract class Adapter {

  def establishConnection(host: String, database: String, username: String, password: String): Connection

  def createQuery(mode: QueryMode.Value): Query
 
  def using[Closeable <: {def close(): Unit}, B](closeable: Closeable)(f: Closeable => B): B =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  
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
        val rs = stmt.getResultSet
        while (rs.next) {
          val o = mapper.map(rs, 1)
          println("RESULT = " + o)
          result ::= o
        }
      }
    }
    result.reverse
  }
  
  def save[T](conn: Connection, modelObject: T, tableName: String): Unit = {
    // TODO cache this
    // TODO get schema name from config
    // TODO wrap jdbc
    // TODO error handling
    // TODO abstract query, fields
    // TODO handle custom primary key / generated key
    println("SAVE get column info for " + tableName)
    var columns = Map.empty[String, Any]
    val dbmd = conn.getMetaData
    val colsRS = dbmd.getColumns(null, "inschr_rer", tableName, null);
    while (colsRS.next) {
      val columnName = colsRS.getString("COLUMN_NAME")
      if (PropertyUtils.isReadable(modelObject, columnName)) {
        columns += (columnName -> PropertyUtils.getSimpleProperty(modelObject, columnName));
      }
    }
    val query = "insert into " + tableName + " (" + columns.map(kv => kv._1).mkString(", ") +
        ") values (" + columns.map(kv => "'" + kv._2 + "'").mkString(", ") + ")"
    println("QUERY = " + query)
    val stmt = conn.createStatement
    stmt.execute(query);
    
    val keyRS = stmt.getGeneratedKeys
    if (keyRS.next) {
      PropertyUtils.setSimpleProperty(modelObject, "id", keyRS.getLong(1))
    }
  }
    
}
