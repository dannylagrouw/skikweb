package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseHelper
import scala.reflect.BeanProperty

class Person extends Base[Person] {

  override val tableName = "people"
    
  @BeanProperty var id: Long = _
  @BeanProperty var first_name: String = _
  @BeanProperty var last_name: String = _
  @BeanProperty var nr: Long = _
  
  @BeanProperty var count_first_name: Long = 0
  
  override def toString = "Person no " + id + ", name = " + first_name + " " + last_name + (if (readonly) " R/O")
  
}

object Person extends Person {
}
