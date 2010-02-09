package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class SaveAndCreate extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testSaveBoolean {
    val p = Person.newFrom(Map('id -> 2))
      
    val b = p.save
    
    assertFalse(b)
  }

  @Test
  def testCreateBoolean {
    val p = Person.create(Map('id -> 2))
    
    assertNotNull(p)
  }

  @Test(expected = classOf[java.sql.SQLException])
  def testSave_! {
    val p = Person.newFrom(Map('id -> 2))
      
    val b = p.save_!
    
    assertFalse(b)
  }

  @Test(expected = classOf[java.sql.SQLException])
  def testCreate_! {
    val p = Person.create_!(Map('id -> 2))
    
    assertNotNull(p)
  }

}
