package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class UsingRawConnection extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testSelectAll {
    val result = Base.execFindQuery(new SqlQuery("select first_name, nr*id as total from people"))
    
    assertEquals(25l, result(4)("total"))
    assertEquals("Piet", result(2)("first_name"))
  }

}
