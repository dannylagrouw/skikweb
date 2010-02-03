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
  
  @Test @Ignore
  def testBigDecimal: Unit = {
    assertEquals(BigDecimal(3), new java.math.BigDecimal("3"))
  }

}
