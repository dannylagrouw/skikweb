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
    val p = PersonWithName2.find(1)
    
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

  @Test
  def testCreatePersonWithName_CompositionAttrSpecified {
    val name = Name("Dwight", "D", "Eisenhower")
    val p = PersonWithName1.create(Map('name -> name, 'nr -> Some(101)))
    
    val p2 = PersonWithName1.findFirst(By('nr -> 101))
    println(p2)
    assertTrue(p2.isDefined)
    assertEquals("Dwight", p2.get.name.first_name)
    assertEquals("D", p2.get.name.middle_name)
    assertEquals("Eisenhower", p2.get.name.last_name)
  }
  
  @Test
  def testUpdatePersonWithName_CompositionAttrSpecified {
    val p = PersonWithName1.find(1)
    p.name = Name("Dwight", "D", "Eisenhower")
    p.save
    
    val p2 = PersonWithName1.find(1)
    println(p2)
    assertEquals("Dwight", p2.name.first_name)
    assertEquals("D", p2.name.middle_name)
    assertEquals("Eisenhower", p2.name.last_name)
  }
  
  @Test
  def testUpdatePersonWithName_CompositionAttrSpecified_EmptyOneField {
    val p = PersonWithName1.find(1)
    p.name = Name("Dwight", null, "Eisenhower")
    p.save
    
    val p2 = PersonWithName1.find(1)
    println(p2)
    assertEquals("Dwight", p2.name.first_name)
    assertEquals(null, p2.name.middle_name)
    assertEquals("Eisenhower", p2.name.last_name)
  }
  
  @Test
  def testCreatePersonWithName_CompositionAttrClassMappingSpecified {
    val name = MappedName("Dwight", "D", "Eisenhower")
    val p = PersonWithName3.create(Map('name -> name, 'nr -> Some(101)))
    
    val p2 = PersonWithName3.findFirst(By('nr -> 101))
    println(p2)
    assertTrue(p2.isDefined)
    assertEquals("Dwight", p2.get.name.first)
    assertEquals("D", p2.get.name.middle)
    assertEquals("Eisenhower", p2.get.name.last)
  }

  @Test
  def testCreatePersonWithName_CompositionAttrClassSpecified {
    val name = Name("Dwight", "D", "Eisenhower")
    val p = PersonWithName2.create(Map('name -> name, 'nr -> Some(101)))
    
    val p2 = PersonWithName2.findFirst(By('nr -> 101))
    println(p2)
    assertTrue(p2.isDefined)
    assertEquals("Dwight", p2.get.name.first_name)
    assertEquals("D", p2.get.name.middle_name)
    assertEquals("Eisenhower", p2.get.name.last_name)
  }

  @Test
  def testCreatePersonWithName_CompositionAttrClassMappingSpecified_NamedParams {
    val name = MappedName("Dwight", "D", "Eisenhower")
    val p = PersonWithName4.create(Map('mappedName -> name, 'nr -> Some(101)))
    
    val p2 = PersonWithName4.findFirst(By('nr -> 101))
    println(p2)
    assertTrue(p2.isDefined)
    assertEquals("Dwight", p2.get.mappedName.first)
    assertEquals("D", p2.get.mappedName.middle)
    assertEquals("Eisenhower", p2.get.mappedName.last)
  }

  @Test
  def testUpdatePersonWithName_CompositionAttrClassMappingSpecified_NamedParams_EmptyOneField {
    val p = PersonWithName4.find(1)
    p.mappedName = MappedName("Dwight", null, "Eisenhower")
    p.save
    
    val p2 = PersonWithName4.find(1)
    println(p2)
    assertEquals("Dwight", p2.mappedName.first)
    assertEquals(null, p2.mappedName.middle)
    assertEquals("Eisenhower", p2.mappedName.last)
  }

  @Test
  def testPersonWithListOfFive {
    Base.execQuery(new SqlQuery("alter table people add column(list_of_five varchar(80))"))

    val p = PersonWithListOfFive.create(Map('last_name -> "Lister", 'list_of_five -> ListOfFive("3,4,5")))
    
    val p2 = PersonWithListOfFive.findFirst(By('last_name -> "Lister"))
    assertTrue(p2.isDefined)
    assertEquals("4", p2.get.list_of_five.list(1))
  }

}
