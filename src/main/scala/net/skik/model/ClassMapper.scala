package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import net.skik.util.LangUtils._
import net.skik.util.ReflectionUtils._

//TODO use reflectionutils for baseClass()
//TODO cache metadata
class ClassMapper[T <: Base[T]](modelClass: Class[T]) extends Mapper[T] {

  //var metaData: ResultSetMetaData = _
  
  override def map(rs: ResultSet) = {
    val o = modelClass.newInstance.asInstanceOf[T]
    if (readonly) o.readonly = true
    for (i <- 1 to metaData.getColumnCount) {
      val columnName = metaData.getColumnName(i)
      if (hasWriteProperty(o.getClass, columnName)) {
        println("MAP iswriteable " + columnName)
        mapProperty(o, columnName, rs.getObject(i))
      } else {
        baseObject[T, BaseObject[T]](modelClass).findCompositionFor(columnName) match {
          case Some(composition) =>
            println("MAP composition " + composition)
            val compositionObject = nullOr(property(o, composition.property.name), composition.compositionClass.newInstance)
            //TODO check field mapping in composition
            mapProperty(compositionObject, columnName, rs.getObject(i))
            setProperty(o, composition.property.name, compositionObject)
          case None =>
            println("MAP no composition for " + columnName)
        }
      }
    }
    o
  }
  
  def mapProperty(modelObject: Object, propertyName: String, columnValue: Object) {
    // TODO cache propertyTypes somewhere:
    val propType = propertyType(modelObject, propertyName).get.getName //TODO catch None
    val propertyValue = propType match {
      case "scala.Option" => if (columnValue == null) None else Some(columnValue)
      case _ => columnValue
    }
    setProperty(modelObject, propertyName, propertyValue)
  }
  
}

object ClassMapper {
  
  private def baseClass(modelClass: String) =
    if (modelClass.takeRight(1) == "$") modelClass.dropRight(1) else modelClass
  
  def apply[T <: Base[T]](modelClass: Class[T]): Mapper[T] = apply(withDo(modelClass.getName) { c =>
    if (c.takeRight(1) == "$") c.dropRight(1) else c
  })

//  def apply[T](modelClass: String): Mapper[T] = new ClassMapper[T](Class.forName(modelClass).asInstanceOf[Class[T]])
  def apply[T <: Base[T]](modelClass: String): Mapper[T] = new ClassMapper[T](Class.forName(baseClass(modelClass)).asInstanceOf[Class[T]])
  
}