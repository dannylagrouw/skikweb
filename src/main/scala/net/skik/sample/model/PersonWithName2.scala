package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

/**
 * Person with name mapped to Name class.
 */
class PersonWithName2 extends Base[PersonWithName2] {

  var id: Int = _
  var name: Name = _
  var nr: Option[Int] = _
  var email: String = _
  
  var count_first_name: Long = 0
  
  override def toString = "PersonWithName2 no " + id + ", name = " + name + (if (readonly) " R/O" else "")

  override def equals(other: Any) = other match {
    case p: PersonWithName2 => p.id == id
    case _ => false
  }
}

object PersonWithName2 extends BaseObject[PersonWithName2] {
  override val tableName = "people"
  composedOf('name, classOf[Name])
}
