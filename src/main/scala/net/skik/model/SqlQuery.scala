package net.skik.model

class SqlQuery(sql: String) extends Query(QueryMode.FindAll) {

  override def toSql = sql
  
}
