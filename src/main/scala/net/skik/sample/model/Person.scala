package net.skik.sample.model

import net.skik.model.Base
import scala.reflect.BeanProperty

class Person extends Base[Person] {

  override val tableName = "people"
    
  @BeanProperty var id: Long = _
  @BeanProperty var first_name: String = _
  @BeanProperty var last_name: String = _
  
  override def toString = "Person no " + id + ", name = " + first_name + " " + last_name
  
}

object Person extends Person {
}
