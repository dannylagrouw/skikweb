package net.skik.model

class MySqlQuery(mode: QueryMode.Value) extends Query(mode) {

  override def toSql: String = {
    // TODO assuming 1 table for now
    //var i = 0
    //var j = 0
    //"select " + tableFields.map { kv => i += 1; kv._2.map(f => "t" + i + "." + f).mkString(", ") }.mkString(", ") +
    //" from " + tableFields.map { kv => j += 1; kv._1 + " t" + j }.mkString(", ") +
    sql match {
      case Some(s) => s.toSql
      case _ =>
        "select " + select.toSql +
        " from " + tableFields.keySet.first +
        conditions.toSql +
        group.toSql +
        having.toSql +
        order.toSql +
        (if (mode == QueryMode.FindFirst) " limit 1" else limit.toSql) +
        offset.toSql
    }
  }

}
