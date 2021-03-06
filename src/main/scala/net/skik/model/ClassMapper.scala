package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import net.skik.util.LangUtils._
import net.skik.util.ReflectionUtils
import net.skik.util.ReflectionUtils._
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class CompositionMapper[A](val composition: Composition[A]) {
  var params = List.empty[Object]
  def addParam(param: Object) {
    params ::= param
  }
  def toInstance: A = {
    val c: java.lang.reflect.Constructor[_] = composition.compositionClass.getConstructors.first
    println("COMPOSITIONMAPPER constructor " + c)
    println("COMPOSITIONMAPPER param types " + c.getParameterTypes.mkString(", "))
    println("COMPOSITIONMAPPER params " + params.reverse)
    c.newInstance(params.reverse.toArray:_*).asInstanceOf[A]
  }
}

//TODO use reflectionutils for baseClass()
//TODO cache metadata
class ClassMapper[T <: Base[T]](modelClass: Class[T]) extends Mapper[T] {

  //var metaData: ResultSetMetaData = _
  
  var baseObject = ReflectionUtils.baseObject[T, BaseObject[T]](modelClass)
  
  def newInstance: T = {
    if (baseObject.parents.isEmpty) {
      modelClass.newInstance.asInstanceOf[T]
    } else {
      Enhancer.create(modelClass, new MethodInterceptor {
        def intercept(obj: Object, method: Method, args: Array[Object], proxy: MethodProxy): Object = {
          println("intercepting " + method.getName)
          baseObject.parents.find(p => method.getName == p.propertyName || method.getName == setterName(p.propertyName)) match {
            case Some(parent) if (method.getName == parent.propertyName) =>
              println("getter called: " + method.getName)
              parent.find(obj.asInstanceOf[T]).asInstanceOf[Object]
            case Some(parent) =>
              println("setter called: " + method.getName); 
              parent.change(obj.asInstanceOf[T], args(0))
            case _ => 
              println("other call: " + method.getName); 
              proxy.invokeSuper(obj, args)
          }
        }
      }).asInstanceOf[T]
    }
  }
  
  override def map(rs: ResultSet) = {
    val o = newInstance
    if (readonly) o.readonly = true
    val compositionMappers = new scala.collection.mutable.HashMap[Composition[T], CompositionMapper[T]]
    for (i <- 1 to metaData.getColumnCount) {
      val columnName = metaData.getColumnName(i)
      baseObject.findCompositionFor(columnName) match {
        case Some(composition: Composition[T]) =>
          println("MAP composition " + composition + " property " + columnName)
          val compositionMapper = compositionMappers.getOrElseUpdate(composition, (new CompositionMapper(composition)))
          compositionMapper.addParam(rs.getObject(i))
//          val compositionObject = nullOr(property(o, composition.property), composition.compositionClass.newInstance)
//          mapProperty(compositionObject, composition.propertyNameFor(columnName), rs.getObject(i))
//          setProperty(o, composition.property, compositionObject)
        case None => 
          if (hasWriteProperty(o.getClass, columnName)) {
            println("MAP iswriteable " + columnName)
            mapProperty(o, columnName, rs.getObject(i))
          } else {
            println("MAP cannot map " + columnName)
          }
      }
    }
    compositionMappers.foreach { entry =>
      val (co, cm) = entry
      mapProperty(o, co.property, cm.toInstance)
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
    println("MAP on " + modelObject.getClass + " property " + propertyName + " -> " + propertyValue + ", from " + columnValue)
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