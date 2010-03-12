package net.skik.util

import net.skik.model.Base
import net.skik.model.BaseObject

object ReflectionUtils {

  def objectClass[A](baseClass: Class[_]): Class[A] =
    Class.forName(baseClass.getName + "$").asInstanceOf[Class[A]]
    
  def baseClass[A](objectClass: Class[_]): Class[A] =
    Class.forName(objectClass.getName.dropRight(1)).asInstanceOf[Class[A]]
    
  def objectProperty[A](baseClass: Class[_], propertyName: String): A = {
    val c = objectClass(baseClass)
    c.getMethod(propertyName).invoke(c.getField("MODULE$").get(null)).asInstanceOf[A]
  }

  def objectInvoke[A](baseClass: Class[_], methodName: String, args: Object*): A = {
    val c = objectClass(baseClass)
    println(c.getMethods.filter(_.getName == methodName).mkString("\n"))
    c.getMethod(methodName, args.map(_.getClass):_*).invoke(c.getField("MODULE$").get(null), args:_*).asInstanceOf[A]
  }
  
  def baseObject[A <: Base[A], B <: BaseObject[A]](baseClass: Class[A]): B = {
    val c = objectClass(baseClass)
    c.getField("MODULE$").get(null).asInstanceOf[B]
  }

  def baseObjectTEMP(baseClass: Class[_]): BaseObject[_] = {
    val c = objectClass(baseClass)
    c.getField("MODULE$").get(null).asInstanceOf[BaseObject[_]]
  }

  def setterName(propertyName: String) = propertyName + "_$eq"

  def getter(c: Class[_], propertyName: String) =
    c.getMethods.find(_.getName == propertyName)
  def setter(c: Class[_], propertyName: String) =
    c.getMethods.find(_.getName == setterName(propertyName))
  
  def hasReadProperty(c: Class[_], propertyName: String) =
    c.getMethods.exists(_.getName == propertyName)
  def hasWriteProperty(c: Class[_], propertyName: String) =
    c.getMethods.exists(_.getName == setterName(propertyName))
  def hasProperty(c: Class[_], propertyName: String) =
    hasReadProperty(c, propertyName) && hasWriteProperty(c, propertyName)
  
  def propertyType(o: {def getClass: Class[_]}, propertyName: String): Option[Class[_]] =
    getter(o.getClass, propertyName).map(_.getReturnType)
    
  def property[B](o: {def getClass: Class[_]}, propertyName: String): B =
    o.getClass.getMethod(propertyName).invoke(o).asInstanceOf[B]

  def setProperty[A](o: {def getClass: Class[_]}, propertyName: String, value: A)(implicit valueClass: Manifest[A]) = 
    try {
      o.getClass.getMethod(setterName(propertyName), valueClass.erasure).invoke(o, value.asInstanceOf[java.lang.Object])
    } catch {
      case e: NoSuchMethodException =>
        setter(o.getClass, propertyName) match {
          case Some(method) => method.invoke(o, value.asInstanceOf[java.lang.Object])
          case None => throw e
        }
    }

  def underscoreToCamel(us: String) = {
    us.split("_").map(_.capitalize).mkString
  }
}
