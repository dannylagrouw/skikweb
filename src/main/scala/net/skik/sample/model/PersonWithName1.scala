package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

/**
 * Person with name mapped, to default class (which is Name).
 */
class PersonWithName1 extends Base[PersonWithName1] {

  var id: Int = _
  var name: Name = _
  var nr: Option[Int] = _
  var email: String = _
  
  var count_first_name: Long = 0
  
  override def toString = "PersonWithName1 no " + id + ", name = " + name + (if (readonly) " R/O" else "")

  override def equals(other: Any) = other match {
    case p: PersonWithName1 => p.id == id
    case _ => false
  }
}

object PersonWithName1 extends BaseObject[PersonWithName1] {
  override val tableName = "people"
  composedOf('name)
}
