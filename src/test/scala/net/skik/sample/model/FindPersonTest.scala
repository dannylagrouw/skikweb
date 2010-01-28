package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class FindPersonTest {

  var lastId = 1
  
  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("delete from people"))
    lastId = 1
  }
  
  @Test
  def testSelectPersonById: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find(2)
    
    assertEquals(2, p.get.asInstanceOf[Person].id)
  }
  
  @Test
  def testSelectAllPeopleById: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Conditions("id = ?", 2))
    assertEquals(1, p.size)
    assertEquals(2, p(0).id)
  }
    
  @Test
  def testSelectAllPeopleByName: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Conditions("first_name = ? and last_name = ?", "Jan", "Jansen"))
        
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).first_name)
    assertEquals("Jansen", p(0).last_name)
  }
    
  @Test
  def testSelectAllPeopleByIdAndName: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Conditions("id = ? and last_name = ?", 1, "Jansen"))
        
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).first_name)
    assertEquals("Jansen", p(0).last_name)
  }

  @Test
  def testSelectAllPeopleByIdAndName_NamedParams: Unit = {
    insertPerson("Jan", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Conditions("id = :id and last_name = :lnm", Map('id -> 1, 'lnm -> "Jansen")))
    
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).first_name)
    assertEquals("Jansen", p(0).last_name)
  }
      
  @Test
  def testSelectAllPeopleByIdAndName_NamedParamsOnly: Unit = {
    insertPerson("Piet", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Conditions(Map('id -> 2, 'last_name -> "Keizer")))
    
    assertEquals(1, p.size)
    assertEquals("Piet", p(0).first_name)
    assertEquals("Keizer", p(0).last_name)
  }

  @Test
  def testSelectAllPeople_OrderByName: Unit = {
    insertPerson("Piet", "Jansen")
    insertPerson("Piet", "Keizer")

    val p = Person.find('all, Order("last_name"))
    
    assertEquals(2, p.size)
    println(p.mkString("\n"))
    assertTrue(p(0).last_name < p(1).last_name)
  }

  @Test
  def testSelectAllPeople_OrderByNameDesc: Unit = {
    insertPerson("Piet", "Jansen")
    insertPerson("Piet", "Keizer")
    
    val p = Person.find('all, Order("last_name desc"))
    
    assertEquals(2, p.size)
    println(p.mkString("\n"))
    assertTrue(p(0).last_name > p(1).last_name)
  }

  @Test
  def testSelectAllPeopleByName_OrderByName: Unit = {
    insertPerson("Piet", "Keizer")
    insertPerson("Jan", "Keizer")

    val p = Person.find('all, Conditions(Map('last_name -> "Keizer")), Order("first_name"))
    
    assertEquals(2, p.size)
    assertTrue(p(0).first_name < p(1).first_name)
  }

  @Test
  def testSelectWithLimit: Unit = {
    insertPerson("Piet", "Keizer")
    insertPerson("Jan", "Keizer")
    insertPerson("Piet", "Jansen")
    val p = Person.find('all, Limit(2))
    assertEquals(2, p.size)
  }
  
  @Test
  def testSelectWithLimitAndOffset: Unit = {
    insertPerson("Piet", "Keizer")
    insertPerson("Jan", "Keizer")
    insertPerson("Piet", "Jansen")
    val p = Person.find('all, Offset(1), Limit(2))
    assertEquals(2, p.size)
  }

  def insertPerson(first: String, last: String): Unit = {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, last_name) values (" + lastId + ", '" + first + "', '" + last + "')"))
    lastId += 1
  }
  
  // findBy('first_name -> "Jan", 'last_name -> "Jansen")
}
