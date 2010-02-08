package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class CreatePersonTest {

  var lastId = 1
  
  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    //Base.execQuery(new SqlQuery("truncate table people"))
    //lastId = 1
  }
  
  @Test
  def testPersonNewSave: Unit = {
    // Rails: p = Person.new; ... ; p.save
    val p = new Person
    p.first_name = "Joop"
    p.last_name = "Jansen"
    val oldId = p.id

    p.save
    
    assertNotSame(p.id, oldId)
  }
  
  @Test
  def testPersonNewSave_FromConstructor: Unit = {
    // Rails: Person.new do { |p| p.name = ...; p.save }
    val p = new Person {
      first_name = "Karel"
      last_name = "Baan"
      save
    }
    
    assertNotSame(0l, p.id)
  }

  @Test
  def testPersonNewSave_FromMap: Unit = {
    // Rails: p = Person.new(:first_name = "a", ...); p.save
    val p = Person.newFrom(Map('first_name -> "Piet", 'last_name -> "Davids"))
    
    p.save
    
    assertNotSame(0, p.id)
    assertEquals("Piet", p.first_name)
    assertEquals("Davids", p.last_name)
  }

  @Test
  def testPersonNewSave_MultipleFromMap: Unit = {
    val ps = Person.newFrom(List(Map('first_name -> "Piet", 'last_name -> "Davids"), Map('first_name -> "Jan", 'last_name -> "Koning")))
    
    ps.foreach(_.save)
    
    ps.foreach { p =>
      assertNotSame(0, p.id)
    }
  }

  @Test
  def testPersonCreate_FromMap: Unit = {
    // Rails: p = Person.create(:first_name = "a", ...)
    val p = Person.create(Map('first_name -> "Piet", 'last_name -> "Davids"))
    
    p match {
      case None => fail("Did not create Person")
      case Some(p) =>
        assertNotSame(0, p.id)
        assertEquals("Piet", p.first_name)
        assertEquals("Davids", p.last_name)
    }
  }

  @Test
  def testPersonCreate_MultipleFromMap: Unit = {
    // Rails: p = Person.new([{:first_name = "a", ...}, {:first_name = "b", ...}])
    val ps = Person.create(List(Map('first_name -> "Piet", 'last_name -> "Davids"), Map('first_name -> "Jan", 'last_name -> "Koning")))
    
    ps.foreach { p =>
      assertNotSame(0, p.id)
    }
  }
}
