package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class UpdatingExistingRows extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testUpdateName {
    val p = Person.find(2)
    p.first_name = "Fred"
      
    p.save
    
    val p2 = Person.find(2)
    assertEquals("Fred", p2.first_name)
  }

  @Test
  def testUpdateAfterFindBySql_KNOWN_ISSUE {
    val p = Person.findBySql("select id, first_name, last_name from people where id = 3").head
    p.first_name = "Wilma"
    
    p.save
    
    val p2 = Person.find(3)
    assertEquals("Wilma", p2.first_name)
    assertEquals("Koning", p2.last_name)
    // TODO Problem:
    assertTrue(p2.nr.isDefined)
    assertEquals(3, p2.nr.get)
  }
  
  @Test
  def testUpdateAttribute {
    val p = Person.find(4)
    
    val b = p.updateAttribute('first_name, "Barney")

    assertTrue(b)
    val p2 = Person.find(4)
    assertEquals("Barney", p2.first_name)
    assertEquals("Keizer", p2.last_name)
  }

  @Test
  def testUpdateAttributes {
    val p = Person.find(4)
    
    val b = p.updateAttributes('first_name -> "Barney", 'email -> "barney@bedrock.com")

    assertTrue(b)
    val p2 = Person.find(4)
    assertEquals("Barney", p2.first_name)
    assertEquals("Keizer", p2.last_name)
    assertEquals("barney@bedrock.com", p2.email)
  }
  
  @Test
  def testUpdateAttributes_Error {
    val p = Person.find(4)
    
    val b = p.updateAttribute('id, 3)

    // TODO Exception or result false?
    assertFalse(b)
  }
  
  @Test
  def testUpdateIdWithAttributes {
    val p = Person.update(2, 'first_name -> "Barney", 'email -> "barney@bedrock.com")
    
    val p2 = Person.find(2)
    assertTrue(p2 == p)
    assertEquals("Barney", p2.first_name)
    assertEquals("Admiraal", p2.last_name)
    assertEquals("barney@bedrock.com", p2.email)
  }
  
  @Test
  def testUpdateAll {
    val result = Person.updateAll("nr = nr * 10", "first_name = 'Piet'")
    
    assertEquals(3, result)
    assertEquals(10, Person.find(1).nr.get)
    assertEquals(20, Person.find(2).nr.get)
    assertEquals(30, Person.find(3).nr.get)
  }
}
