package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class FindBySqlTest {

  var lastId = 1
  
  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("delete from people"))
    lastId = 1
  }

  def insertPerson(first: String, last: String): Unit = {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, last_name) values (" + lastId + ", '" + first + "', '" + last + "')"))
    lastId += 1
  }

  @Test
  def testFindBySql: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val ps = Person.findBySql("select people.* from people where first_name in ('Jan', 'Piet')")
    
    assertEquals(2, ps.size)
    ps.foreach{ p =>
      assertNotSame(0, p.id)
      assertNotNull(p.first_name)
      assertNotNull(p.last_name)
    }
  }

  @Test
  def testFindBySql_PartialFields: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")
    
    val ps = Person.findBySql("select first_name, id from people where first_name in ('Jan', 'Piet')")
    
    assertEquals(2, ps.size)
    ps.foreach{ p =>
      assertNotSame(0, p.id)
      assertNotNull(p.first_name)
      assertNull(p.last_name)
    }
  }

  @Test
  def testFindBySql_AggregateQueries: Unit = {
    insertPerson("Piet", "Keizer")
    insertPerson("Piet", "Admiraal")
    insertPerson("Piet", "Koning")
    insertPerson("Jan", "Keizer")
    insertPerson("Jan", "Davids")
    val ps = Person.findBySql("select first_name, count(*) as id from people group by first_name order by 2 desc")
    
    assertEquals(2, ps.size)
    assertEquals(3, ps(0).id)
    assertEquals("Piet", ps(0).first_name)
    assertEquals(2, ps(1).id)
    assertEquals("Jan", ps(1).first_name)
  }
  
  @Test
  def testFindBySql_ParamList: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Jan", "Keizer")
    insertPerson("Piet", "Keizer")
    
    val ps = Person.findBySql("select * from people where id = ? and first_name = ?", 2, "Jan")

    assertEquals(1, ps.size)
    assertEquals(2, ps(0).id)
  }
  
  @Test
  def testFindBySql_NamedParams: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Jan", "Keizer")
    insertPerson("Piet", "Keizer")
    
    val ps = Person.findBySql("select * from people where id = :id and first_name = :fnm", Map('id -> 1, 'fnm -> "Jan"))

    assertEquals(1, ps.size)
    assertEquals(1, ps(0).id)
  }
  
}
