package net.skik.model

class MySqlQuery(mode: QueryMode.Value) extends Query(mode) {

  override def toSql: String = {
    var i = 0
    var j = 0
    "select " + tableFields.map { kv => i += 1; kv._2.map(f => "t" + i + "." + f).mkString(", ") }.mkString(", ") +
      " from " + tableFields.map { kv => j += 1; kv._1 + " t" + j }.mkString(", ") +
      conditions.toSql + 
      order.toSql +
      (if (mode == QueryMode.FindFirst) " limit 1" else limit.toSql) +
      offset.toSql
//    "select * from people where id = 1"
  }

}
