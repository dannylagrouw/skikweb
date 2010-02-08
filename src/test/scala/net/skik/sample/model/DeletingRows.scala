package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class DeletingRows extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testDeleteById {
    val n = Person.delete(2)
      
    assertEquals(1, n)
    val p = Person.findFirst(Conditions("id = ?", 2))
    assertEquals(None, p)
  }

  @Test
  def testDeleteByIds {
    val n = Person.delete(List(2, 3, 4))
      
    assertEquals(3, n)
    val p = Person.findAll(Conditions("id in (?,?,?)", 2, 3, 4))
    assertEquals(List.empty[Int], p)
  }
  
  @Test
  def testDestroy {
    val p = Person.find(4)
    
    p.destroy
    
    assertTrue(p.frozen)
    val p2 = Person.findFirst(Conditions("id = ?", 4))
    assertEquals(None, p2)
  }

  @Test
  def testDestroyAll {
    Person.destroyAll(Conditions("nr > ?", 3))
    
    val p2 = Person.findFirst(Conditions("nr > 3"))
    assertEquals(None, p2)
  }
}
