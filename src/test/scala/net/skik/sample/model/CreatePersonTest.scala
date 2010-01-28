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
    //Base.execQuery(new SqlQuery("delete from people"))
    lastId = 1
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
  }
}
