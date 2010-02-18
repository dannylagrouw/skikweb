package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class ComposingData extends AbstractModelTest {

  override def insertFixture {
    insertPersonWithName("Piet", "de", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  def insertPersonWithName(first: String, middle: String, last: String, nr: Long) {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, middle_name, last_name, nr) values (" + 
        lastId + ", '" + first + "', '" + middle + "', '" + last + "'," + nr + ")"))
    lastId += 1
  }

  @Test
  def testFindPersonWithName_CompositionAttrSpecified {
    val p = PersonWithName1.find(1)
    
    assertEquals("Piet", p.name.first_name)
    assertEquals("de", p.name.middle_name)
    assertEquals("Keizer", p.name.last_name)
    assertEquals(Some(1), p.nr)
  }
  
  @Test
  def testFindPersonWithName_CompositionAttrClassSpecified {
    val p = PersonWithName.find(1)
    
    assertEquals("Piet", p.name.first_name)
    assertEquals("de", p.name.middle_name)
    assertEquals("Keizer", p.name.last_name)
    assertEquals(Some(1), p.nr)
  }

  @Test
  def testFindPersonWithName_CompositionAttrClassMappingSpecified {
    val p = PersonWithName3.find(1)
    
    assertEquals("Piet", p.name.first)
    assertEquals("de", p.name.middle)
    assertEquals("Keizer", p.name.last)
    assertEquals(Some(1), p.nr)
  }

  @Test
  def testFindPersonWithName_CompositionAttrClassMappingSpecified_NamedParams {
    val p = PersonWithName4.find(1)
    
    assertEquals("Piet", p.mappedName.first)
    assertEquals("de", p.mappedName.middle)
    assertEquals("Keizer", p.mappedName.last)
    assertEquals(Some(1), p.nr)
  }

}
