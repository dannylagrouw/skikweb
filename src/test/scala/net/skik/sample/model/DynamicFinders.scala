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
    
    assertTrue(p.isDefined)
    assertEquals(2, p.get.nr.get)
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
        
    assertTrue(p.isDefined)
    assertEquals(1, p.get.nr.get)
    assertEquals("Keizer", p.get.last_name)
  }
  
  @Test
  def testFindAllByFirstNameAndNr {
    val ps = Person.findAll(By('first_name -> "Piet"), Conditions("nr > ?", 1))
        
    assertEquals(2, ps.size)
    ps.foreach(p => assertEquals("Piet", p.first_name))
    ps.foreach(p => assertTrue(p.nr.get > 1))
  }

  @Test
  def testFindAllByFirstNameAndNr_Limit {
    val ps = Person.findAll(By('first_name -> "Piet"), Conditions("nr > ?", 1), Limit(1))
        
    assertEquals(1, ps.size)
  }

  @Test
  def testFindOrInitializeById_Found {
    val p = Person.findOrInitialize(By('id -> 3))
    
    assertEquals(("Piet", "Koning"), (p.first_name, p.last_name))
  }
  
  @Test
  def testFindOrInitializeById_NotFound {
    val p = Person.findOrInitialize(By('id -> 6))
    
    assertEquals(6, p.id)
  }
  
  @Test
  def testFindOrCreateById_Found {
    val p = Person.findOrCreate(By('id -> 3))
    
    assertEquals(("Piet", "Koning"), (p.first_name, p.last_name))
  }
  
  @Test
  def testFindOrCreateById_NotFound {
    val p = Person.findOrCreate(By('id -> 6, 'first_name -> "Klaas", 'last_name -> "Boon"))
    
    assertEquals(6, p.id)
    assertEquals("Klaas", p.first_name)
    assertEquals("Boon", p.last_name)
  }
  
}
