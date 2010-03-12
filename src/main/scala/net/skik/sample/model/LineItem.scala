package net.skik.sample.model

import net.skik.model.Base
import net.skik.model.BaseObject

class LineItem extends Base[LineItem] {

  var id: Int = _
  var quantity: Int = _
  var total_price: Option[BigDecimal] = None
  var order_id: Long = _
  var order: Order = _
  //def order: Option[Order] = order_id.map(Order.find(_))
  var product_id: Long = _

  var product: Product = _
//  def product: Product = Product.find(product_id)
//  def product_=(otherProduct: Product) {
//    product_id = otherProduct.id
//  }
  
  override def toString = "LineItem no " + id + ", quantity " + quantity +
      ", total_price " + total_price + ", product " + product + ", order " + order

}

object LineItem extends BaseObject[LineItem] {
  override val tableName = "line_items"
  belongsTo('order, classOf[Order], "order_id", null)
  belongsTo('product, classOf[Product], "product_id", null)
}
