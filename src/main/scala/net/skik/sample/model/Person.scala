package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject
import scala.reflect.BeanProperty

class Person extends Base[Person] {

  @BeanProperty var id: Int = _
  @BeanProperty var first_name: String = _
  @BeanProperty var last_name: String = _
  @BeanProperty var nr: Option[Int] = _
  @BeanProperty var email: String = _
  
  @BeanProperty var count_first_name: Long = 0
  
  override def toString = "Person no " + id + ", name = " + first_name + " " + last_name + (if (readonly) " R/O" else "")

}

object Person extends BaseObject[Person] {
  override val tableName = "people"
}
