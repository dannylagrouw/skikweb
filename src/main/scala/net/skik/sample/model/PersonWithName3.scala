package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

class PersonWithName3 extends Base[PersonWithName3] {

  var id: Int = _
  var name: MappedName = _
  var nr: Option[Int] = _
  
  override def toString = "PersonWithName3 no " + id + ", name = " + name + (if (readonly) " R/O" else "")

  override def equals(other: Any) = other match {
    case p: PersonWithName3 => p.id == id
    case _ => false
  }
}

object PersonWithName3 extends BaseObject[PersonWithName3] {
  override val tableName = "people"
  composedOf('name, classOf[MappedName], List(('first_name, 'first), ('middle_name, 'middle), ('last_name, 'last)))
}
