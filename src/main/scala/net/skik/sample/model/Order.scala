package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject

class Order extends Base[Order] {

  var id: Int = _
  var name: String = _
  var address: String = _
  var email: String = _
  var pay_type: String = _
  
  override def toString = "Order no " + id

}

object Order extends BaseObject[Order] {
  override val tableName = "orders"
}
