package net.skik.util

object ReflectionUtils {

  def objectClass[A](baseClass: Class[_]): Class[A] =
    Class.forName(baseClass.getName + "$").asInstanceOf[Class[A]]
    
  def baseClass[A](objectClass: Class[_]): Class[A] =
    Class.forName(objectClass.getName.dropRight(1)).asInstanceOf[Class[A]]
    
  def objectProperty[A](baseClass: Class[_], propertyName: String): A = {
    val c = objectClass(baseClass)
    c.getMethod(propertyName).invoke(c.getField("MODULE$").get(null)).asInstanceOf[A]
  }
  
  def property[B](o: {def getClass: Class[_]}, propertyName: String): B =
    o.getClass.getMethod(propertyName).invoke(o).asInstanceOf[B]

  def setProperty[A](o: {def getClass: Class[_]}, propertyName: String, value: A)(implicit valueClass: Manifest[A]) = 
    try {
      o.getClass.getMethod(propertyName + "_$eq", valueClass.erasure).invoke(o, value.asInstanceOf[java.lang.Object])
    } catch {
      case e: NoSuchMethodException =>
        o.getClass.getMethods.find(_.getName == propertyName + "_$eq") match {
          case Some(method) => method.invoke(o, value.asInstanceOf[java.lang.Object])
          case None => throw e
        }
    }
  
}
