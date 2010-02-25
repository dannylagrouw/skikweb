package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class ObjectIdentity extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  /**
   * The equals method considers p1 to be not a Person object.
   * Either fix Base#equals or forbid new Object {...} construction.
   */
  @Test
  def testEquals_KNOWN_ISSUE {
    val p1 = new Person { id = 1 }
    val p2 = Person.find(1)
    
    assertTrue(p2.equals(p1))
  }

  @Test
  def testEquals_BothFromDb {
    val p1 = Person.find(1)
    val p2 = Person.find(1)
    
    assertTrue(p2.equals(p1))
  }

  @Test
  def testEquals_DifferentIds {
    val p1 = Person.find(1)
    p1.id = 2
    val p2 = Person.find(1)
    
    assertFalse(p2.equals(p1))
  }

  @Test
  def testEquals_DifferentTypes {
    val p1 = PersonWithName1.find(1)
    val p2 = Person.find(1)
    
    assertFalse(p2.equals(p1))
  }

}
