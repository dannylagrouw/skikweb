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
    insertInto(PlacedOrder, 'name -> "Roosevelt")
    insertInto(LineItem, 'product_id -> 1, 'order_id -> 1, 'quantity -> 2, 'total_price -> 18.00)
  }
  
  @Test
  def testFindParent_Product {
    val product = Product.find(1)

    val lineItem = LineItem.find(1)
    
    assertEquals(product, lineItem.product)
  }

  @Test
  def testFindParent_Order {
    val order = PlacedOrder.find(1)

    val lineItem = LineItem.find(1)
    
    assertEquals(order, lineItem.order)
  }

  @Test
  def testUpdateParent_Product {
    val product2 = Product.find(2)
    val lineItem = LineItem.find(1)

    lineItem.product = product2
    lineItem.save
    
    val lineItem2 = LineItem.find(1)
    assertEquals(product2, lineItem2.product)
  }

  @Test
  def testUpdateParent_Order {
    val order2 = PlacedOrder.find(2)
    val lineItem = LineItem.find(1)

    lineItem.order = order2
    lineItem.save
    
    val lineItem2 = LineItem.find(1)
    assertEquals(order2, lineItem2.order)
  }
}
