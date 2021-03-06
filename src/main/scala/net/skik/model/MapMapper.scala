package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

class MapMapper extends Mapper[Map[String, Any]] {

  override def map(rs: ResultSet) = {
    var m = Map.empty[String, Any]
    for (i <- 1 to metaData.getColumnCount) {
      val columnName = metaData.getColumnName(i)
      m += columnName -> rs.getObject(i)
    }
    m
  }
  
}
