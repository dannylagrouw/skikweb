package net.skik.model

import java.sql.Connection

abstract class Adapter {

  def establishConnection(host: String, database: String, username: String, password: String): Connection

  def createQuery(mode: QueryMode.Value): Query
 
  def using[Closeable <: {def close(): Unit}, B](closeable: Closeable)(f: Closeable => B): B =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  
  def execute(conn: Connection, query: Query, mapper: Mapper): List[Any] = {
    println("QUERY = " + query.toSql)
    println("ARGS = " + query.whereArgs.mkString(","))
    val stmt = conn.prepareStatement(query.toSql)
    var result = List.empty[Any]
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
  
}
