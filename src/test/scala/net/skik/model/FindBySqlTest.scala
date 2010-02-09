package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class FindBySqlTest extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }
  
  @Test
  def testFindBySql {
    val ps = Person.findBySql("select * from people where last_name in ('Koning', 'Keizer')")
    
    assertEquals(3, ps.size)
    ps.foreach{ p =>
      assertNotSame(0, p.id)
      assertTrue(p.first_name == "Jan" || p.first_name == "Piet")
      assertTrue(p.last_name == "Koning" || p.last_name == "Keizer")
    }
  }

  @Test
  def testFindBySql_PartialFields {
    val ps = Person.findBySql("select first_name, id from people where first_name in ('Jan', 'Piet')")
    
    assertEquals(5, ps.size)
    ps.foreach{ p =>
      assertNotSame(0, p.id)
      assertNotNull(p.first_name)
      assertNull(p.last_name)
    }
  }

  @Test
  def testFindBySql_AggregateQueries {
    val ps = Person.findBySql("select first_name, count(*) as count_first_name from people group by first_name order by 2 desc")
    
    assertEquals(2, ps.size)
    assertEquals(3, ps(0).count_first_name)
    assertEquals("Piet", ps(0).first_name)
    assertEquals(2, ps(1).count_first_name)
    assertEquals("Jan", ps(1).first_name)
  }
  
  @Test
  def testFindBySql_ParamList {
    val ps = Person.findBySql("select * from people where id = ? and first_name = ?", 2, "Piet")

    assertEquals(1, ps.size)
    assertEquals(2, ps(0).id)
  }
  
  @Test
  def testFindBySql_NamedParams {
    val ps = Person.findBySql("select * from people where id = :id and first_name = :fnm", Map('id -> 1, 'fnm -> "Piet"))

    assertEquals(1, ps.size)
    assertEquals(1, ps(0).id)
  }
  
}
