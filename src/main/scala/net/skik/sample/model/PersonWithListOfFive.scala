package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

/**
 * Person with list_of_five mapped.
 */
class PersonWithListOfFive extends Base[PersonWithListOfFive] {

  var id: Int = _
  var last_name: String = _
  var list_of_five: ListOfFive = _
  var nr: Option[Int] = _
  var email: String = _
  
  var count_first_name: Long = 0
  
  override def toString = "PersonWithListOfFive no " + id + ", list = " + list_of_five

  override def equals(other: Any) = other match {
    case p: PersonWithListOfFive => p.id == id
    case _ => false
  }
}

object PersonWithListOfFive extends BaseObject[PersonWithListOfFive] {
  override val tableName = "people"
  composedOf('list_of_five, classOf[ListOfFive])
}
