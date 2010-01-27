package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

class ClassMapper(modelClass: Class[_]) extends Mapper {

  //var metaData: ResultSetMetaData = _
  
  override def map(rs: ResultSet, n: Int) = {
    val o = modelClass.newInstance
    for (i <- 1 to metaData.getColumnCount) {
      val columnName = metaData.getColumnName(i)
      if (PropertyUtils.isWriteable(o, columnName)) {
        PropertyUtils.setSimpleProperty(o, columnName, rs.getObject(i));
      }
    }
    o
  }
  
}

object ClassMapper {
  
  def apply(modelClass: Class[_]): Mapper = apply(withDo(modelClass.getName) { c =>
    if (c.takeRight(1) == "$") c.dropRight(1) else c
  })

  def apply(modelClass: String) = new ClassMapper(Class.forName(modelClass))
  
}