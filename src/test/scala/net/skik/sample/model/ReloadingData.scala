package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class ReloadingData extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testReload {
    var p = Person.findFirst(By('last_name -> "Admiraal")).get
    Base.execQuery(new SqlQuery("update people set last_name = 'Boon' where last_name = 'Admiraal'"))

    p = p.reload
    
    assertEquals("Boon", p.last_name)
  }
  
}
