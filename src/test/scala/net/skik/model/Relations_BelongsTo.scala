package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model.{Order => PlacedOrder}
import net.skik.sample.model._

class Relations_BelongsTo extends AbstractRelationsTest {

  def insertFixture {
    insertInto(Product, 'title -> "Pecan Pie", 'price -> 9.95)
    insertInto(Product, 'title -> "Strawberry Pie", 'price -> 12.50)
    insertInto(PlacedOrder, 'name -> "Eisenhower")
    insertInto(LineItem, 'product_id -> 1, 'order_id -> 1, 'quantity -> 2, 'total_price -> 18.00)
  }
  
  @Test
  def testHello {
    val product = Product.find(1)
    val lineItem = LineItem.find(1)
    
    println(LineItem.parents.mkString("\n"))
    assertEquals(product, lineItem.product)
  }
}
