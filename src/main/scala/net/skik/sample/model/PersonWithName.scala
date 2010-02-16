package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

class PersonWithName extends Base[PersonWithName] {

  var id: Int = _
  var name: Name = _
  var nr: Option[Int] = _
  var email: String = _
  
  var count_first_name: Long = 0
  
  override def toString = "PersonWithName no " + id + ", name = " + name + (if (readonly) " R/O")

  override def equals(other: Any) = other match {
    case p: PersonWithName => p.id == id
    case _ => false
  }
}

object PersonWithName extends BaseObject[PersonWithName] {
  override val tableName = "people"
  composedOf('name, classOf[Name])
}
