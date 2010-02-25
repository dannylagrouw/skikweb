package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject

class Product extends Base[Product] {

  var id: Int = _
  var title: String = _
  var description: String = _
  var image_url: String = _
  var price: Option[BigDecimal] = None
  
  override def toString = "Product no " + id + ", title " + title + ", price " + price

}

object Product extends BaseObject[Product] {
  override val tableName = "products"
}
