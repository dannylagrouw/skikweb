package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

class PersonWithName4 extends Base[PersonWithName4] {

  var id: Int = _
  var mappedName: MappedName = _
  var nr: Option[Int] = _
  
  override def toString = "PersonWithName4 no " + id + ", name = " + mappedName + (if (readonly) " R/O" else "")

  override def equals(other: Any) = other match {
    case p: PersonWithName4 => p.id == id
    case _ => false
  }
}

object PersonWithName4 extends BaseObject[PersonWithName4] {
  override val tableName = "people"
  composedOf('mappedName, mapping = List(('first_name, 'first), ('middle_name, 'middle), ('last_name, 'last)))
}
