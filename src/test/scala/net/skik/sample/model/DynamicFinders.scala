package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class DynamicFinders extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testFindByLastName {
    val p = Person.findFirst(By('last_name -> "Admiraal"))
        
    assertEquals(2, p.nr)
  }
  
  @Test
  def testFindAllByLastName {
    val ps = Person.findAll(By('last_name -> "Keizer"))
        
    assertEquals(2, ps.size)
    ps.foreach(p => assertEquals("Keizer", p.last_name))
  }
  
  @Test
  def testFindByFirstAndLastName {
    val p = Person.findFirst(By('first_name -> "Piet", 'last_name -> "Keizer"))
        
    assertEquals(1, p.nr)
    assertEquals("Keizer", p.last_name)
  }
  
  @Test
  def testFindAllByFirstNameAndNr {
    val ps = Person.findAll(By('first_name -> "Piet"), Conditions("nr > ?", 1))
        
    assertEquals(2, ps.size)
    ps.foreach(p => assertEquals("Piet", p.first_name))
    ps.foreach(p => assertTrue(p.nr > 1))
  }

  @Test
  def testFindAllByFirstNameAndNr_Limit {
    val ps = Person.findAll(By('first_name -> "Piet"), Conditions("nr > ?", 1), Limit(1))
        
    assertEquals(1, ps.size)
  }

}
