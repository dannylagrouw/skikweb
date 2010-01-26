package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class PersonTest {

  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
  }
  
  @Test
  def testSelectPersonById: Unit = {
    assertEquals(328, Person.find(328).get.asInstanceOf[Person].id)
  }
  
  @Test
  def testSelectAllPeopleById: Unit = {
    val p = Person.find('all, Conditions("id = ?", 328))
    assertEquals(1, p.size)
    assertEquals(328, p(0).asInstanceOf[Person].id)
  }
    
  @Test
  def testSelectAllPeopleByName: Unit = {
    val p = Person.find('all, Conditions("first_name = ? and last_name = ?", "Jan", "Jansen"))
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).asInstanceOf[Person].first_name)
    assertEquals("Jansen", p(0).asInstanceOf[Person].last_name)
  }
    
  @Test
  def testSelectAllPeopleByIdAndName: Unit = {
    val p = Person.find('all, Conditions("id = ? and last_name = ?", 1, "Jansen"))
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).asInstanceOf[Person].first_name)
    assertEquals("Jansen", p(0).asInstanceOf[Person].last_name)
  }

  @Test
  def testSelectAllPeopleByIdAndName_NamedParams: Unit = {
    val p = Person.find('all, Conditions("id = :id and last_name = :lnm", Map('id -> 1, 'lnm -> "Jansen")))
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).asInstanceOf[Person].first_name)
    assertEquals("Jansen", p(0).asInstanceOf[Person].last_name)
  }
      
  @Test
  def testSelectAllPeopleByIdAndName_NamedParamsOnly: Unit = {
    val p = Person.find('all, Conditions(Map('id -> 328, 'last_name -> "Keizer")))
    assertEquals(1, p.size)
    assertEquals("Piet", p(0).asInstanceOf[Person].first_name)
    assertEquals("Keizer", p(0).asInstanceOf[Person].last_name)
  }

  @Test
  def testSelectAllPeople_OrderByName: Unit = {
    //TODO separate fixture
    val p = Person.find('all, Order("last_name"))
    assertEquals(2, p.size)
    println(p.mkString("\n"))
    assertTrue(p(0).asInstanceOf[Person].last_name < p(1).asInstanceOf[Person].last_name)
  }

  @Test
  def testSelectAllPeople_OrderByNameDesc: Unit = {
    //TODO separate fixture
    val p = Person.find('all, Order("last_name desc"))
    assertEquals(2, p.size)
    println(p.mkString("\n"))
    assertTrue(p(0).asInstanceOf[Person].last_name > p(1).asInstanceOf[Person].last_name)
  }

  @Test
  def testSelectAllPeopleByName_OrderByName: Unit = {
    val p = Person.find('all, Conditions(Map('last_name -> "Keizer")), Order("first_name"))
    assertEquals(2, p.size)
    println(p.mkString("\n"))
    assertTrue(p(0).asInstanceOf[Person].first_name < p(1).asInstanceOf[Person].first_name)
  }
  
  // findBy('first_name -> "Jan", 'last_name -> "Jansen")
}
