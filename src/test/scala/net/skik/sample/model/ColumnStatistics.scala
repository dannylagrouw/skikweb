package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class ColumnStatistics {

  var lastId = 1

  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("delete from people"))
    lastId = 1
    insertFixture
  }

  def insertPerson(first: String, last: String, nr: Long): Unit = {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, last_name, nr) values (" + lastId + ", '" + first + "', '" + last + "'," + nr + ")"))
    lastId += 1
  }

  def insertFixture: Unit = {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testCount: Unit = {
    val c = Person.count
    
    assertEquals(5, c)
  }
  
  @Test
  def testCountFirstname: Unit = {
    val c = Person.count('first_name)
    
    assertEquals(5l, c)
  }
  
  @Test
  def testCount_Distinct: Unit = {
    val c = Person.count('first_name, Distinct(true))
    
    assertEquals(2l, c)
  }
  
  @Test
  def testCount_WithCondition: Unit = {
    val c = Person.count(Conditions("first_name = ?", "Piet"))
    
    assertEquals(3, c)
  }

  @Test
  def testAverage: Unit = {
    val c = Person.average('nr)
    
    assertEquals(3, c.intValue)
  }
  
  @Test
  def testAverage_WithCondition: Unit = {
    val c = Person.average('nr, Conditions("first_name = ?", "Piet"))
        
    assertEquals(2, c.intValue)
  }
    
  @Test
  def testMinimum_WithCondition: Unit = {
    val c = Person.minimum('nr, Conditions("first_name = ?", "Piet"))
        
    assertEquals(1, c)
  }
    
  @Test
  def testMaximum_WithCondition: Unit = {
    val c = Person.maximum('nr, Conditions("first_name = ?", "Piet"))
        
    assertEquals(3, c)
  }
    
  @Test
  def testSum_WithCondition: Unit = {
    val c = Person.sum('nr, Conditions("first_name = ?", "Piet"))
        
    assertEquals(6, c.intValue)
  }

  @Test
  def testCalculate {
    val c = Person.calculate('std, 'nr)
    
    assertEquals(math.sqrt(2), c.doubleValue, .0001)
  }
  
  @Test
  def testCountGrouped {
    val cs = Person.countGrouped('nr, Group("first_name"), Order("first_name"))

    assertEquals(2, cs.size)
    assertEquals("Jan", cs(0)._1)
    assertEquals(2l, cs(0)._2)
    assertEquals("Piet", cs(1)._1)
    assertEquals(3l, cs(1)._2)
  }
  
  @Test
  def testAverageGrouped {
    val cs = Person.averageGrouped('nr, Group("first_name"), Order("first_name"))

    assertEquals(2, cs.size)
    assertEquals("Jan", cs(0)._1)
    assertEquals(4.5, cs(0)._2.doubleValue, .001)
    assertEquals("Piet", cs(1)._1)
    assertEquals(2, cs(1)._2.intValue)
  }
  
  @Test
  def testMinimumGrouped {
    val cs = Person.minimumGrouped('nr, Group("first_name"), Order("first_name"))

    assertEquals(2, cs.size)
    assertEquals("Jan", cs(0)._1)
    assertEquals(4, cs(0)._2)
    assertEquals("Piet", cs(1)._1)
    assertEquals(1, cs(1)._2)
  }
  
  @Test
  def testMaximumGrouped {
    val cs = Person.maximumGrouped('nr, Group("first_name"), Order("first_name"))

    assertEquals(2, cs.size)
    assertEquals("Jan", cs(0)._1)
    assertEquals(5, cs(0)._2)
    assertEquals("Piet", cs(1)._1)
    assertEquals(3, cs(1)._2)
  }
  
  @Test(expected = classOf[RuntimeException])
  def testMaximumGrouped_ForgotGroup {
    Person.maximumGrouped('nr, Order("first_name"))
  }
  
  @Test
  def testMaximumGrouped_Top3 {
    insertPerson("Dries", "Bakker", 8)
    insertPerson("Joop", "Boon", 9)
    insertPerson("Klaas", "Keizer", 10)
    
    val cs = Person.maximumGrouped('nr, Group("first_name"), Limit(3), Order("max(nr) desc"))

    assertEquals(3, cs.size)
    assertEquals(("Klaas", 10), cs(0))
    assertEquals(("Joop", 9), cs(1))
    assertEquals(("Dries", 8), cs(2))
  }
  
  @Test
  def testSumGrouped {
    val cs = Person.sumGrouped('nr, Group("first_name"), Order("first_name"))

    assertEquals(2, cs.size)
    assertEquals("Jan", cs(0)._1)
    assertEquals(9, cs(0)._2.intValue)
    assertEquals("Piet", cs(1)._1)
    assertEquals(6, cs(1)._2.intValue)
  }
  
  @Test @Ignore
  def testBigDecimal: Unit = {
    assertEquals(BigDecimal(3), new java.math.BigDecimal("3"))
  }

}
