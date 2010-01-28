package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

class ClassMapper[T](modelClass: Class[T]) extends Mapper[T] {

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
  
  private def baseClass(modelClass: String) =
    if (modelClass.takeRight(1) == "$") modelClass.dropRight(1) else modelClass
  
  def apply[T](modelClass: Class[T]): Mapper[T] = apply(withDo(modelClass.getName) { c =>
    if (c.takeRight(1) == "$") c.dropRight(1) else c
  })

//  def apply[T](modelClass: String): Mapper[T] = new ClassMapper[T](Class.forName(modelClass).asInstanceOf[Class[T]])
  def apply[T](modelClass: String): Mapper[T] = new ClassMapper[T](Class.forName(baseClass(modelClass)).asInstanceOf[Class[T]])
  
}