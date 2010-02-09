package net.skik.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

class ReadingExistingRows extends AbstractModelTest {

  override def insertFixture {
    insertPerson("Piet", "Keizer", 1)
    insertPerson("Piet", "Admiraal", 2)
    insertPerson("Piet", "Koning", 3)
    insertPerson("Jan", "Keizer", 4)
    insertPerson("Jan", "Davids", 5)
  }

  @Test
  def testSelectPersonById {
    val p = Person.find(2)
    
    assertEquals(2, p.id)
  }
  
  @Test(expected = classOf[net.skik.model.RecordNotFound])
  def testSelectPeopleById_ExceptionWhenIdNotFound {
    val ps = Person.find(6)
  }
  
  @Test
  def testSelectPeopleByIds {
    val ps = Person.find(3, 4, 1)
    
    assertEquals(3, ps.size)
    ps.foreach(p => assertTrue(p.id == 1 || p.id == 3 || p.id == 4))
  }
  
  @Test(expected = classOf[net.skik.model.RecordNotFound])
  def testSelectPeopleByIds_ExceptionWhenIdNotFound {
    val ps = Person.find(1, 6)
  }
  
  @Test
  def testSelectAllPeopleById {
    val p = Person.find('all, Conditions("id = ?", 2))
    assertEquals(1, p.size)
    assertEquals(2, p(0).id)
  }
    
  @Test
  def testSelectAllPeopleByName {
    val p = Person.find('all, Conditions("first_name = ? and last_name = ?", "Jan", "Davids"))
        
    assertEquals(1, p.size)
    assertEquals("Jan", p(0).first_name)
    assertEquals("Davids", p(0).last_name)
  }
    
  @Test
  def testSelectAllPeopleByIdAndName {
    val p = Person.find('all, Conditions("id = ? and last_name = ?", 1, "Keizer"))
        
    assertEquals(1, p.size)
    assertEquals("Piet", p(0).first_name)
    assertEquals("Keizer", p(0).last_name)
  }

  @Test
  def testSelectAllPeopleByIdAndName_NamedParams {
    val p = Person.find('all, Conditions("id = :id and last_name = :lnm", Map('id -> 1, 'lnm -> "Keizer")))
    
    assertEquals(1, p.size)
    assertEquals("Piet", p(0).first_name)
    assertEquals("Keizer", p(0).last_name)
  }
      
  @Test
  def testSelectAllPeopleByIdAndName_NamedParamsOnly {
    val p = Person.find('all, Conditions(Map('id -> 1, 'last_name -> "Keizer")))
    
    assertEquals(1, p.size)
    assertEquals("Piet", p(0).first_name)
    assertEquals("Keizer", p(0).last_name)
  }

  @Test
  def testSelectAllPeople_OrderByName {
    val p = Person.find('all, Order("last_name"))
    
    assertEquals(5, p.size)
    println(p.mkString("\n"))
    for (i <- 0 until 4) {
      assertTrue(p(i).last_name <= p(i + 1).last_name)
    }
  }

  @Test
  def testSelectAllPeople_OrderByNameDesc {
    val p = Person.find('all, Order("last_name desc"))
    
    assertEquals(5, p.size)
    println(p.mkString("\n"))
    for (i <- 0 until 4) {
      assertTrue(p(i).last_name >= p(i + 1).last_name)
    }
  }

  @Test
  def testSelectAllPeopleByName_OrderByName {
    val p = Person.find('all, Conditions(Map('last_name -> "Keizer")), Order("first_name"))
    
    assertEquals(2, p.size)
    assertEquals("Keizer", p(0).last_name)
    assertEquals("Keizer", p(1).last_name)
    assertTrue(p(0).first_name < p(1).first_name)
  }

  @Test
  def testSelectWithLimit {
    val p = Person.find('all, Limit(2))
        
    assertEquals(2, p.size)
  }
  
  @Test
  def testSelectWithLimitAndOffset {
    val p = Person.find('all, Offset(1), Limit(2))
        
    assertEquals(2, p.size)
  }

  @Test
  def testSelectPersonById_SomeFields {
    val p = Person.findFirst(Select("id, first_name"), Conditions("id = 2"))
    
    assertTrue(p.isDefined)
    assertEquals(2, p.get.id)
	assertEquals("Piet", p.get.first_name)
	assertNull(p.get.last_name)
  }

  @Test
  def testSelectPerson_Readonly {
    val ps = Person.findAll(Readonly(true))
    
    assertEquals(5, ps.size)
    ps.foreach { p =>
      assertTrue(p.readonly)
    }
  }
 
  @Test
  def testSelectAllPeopleByName_GroupByName {
    val ps = Person.findAll(
        Select("first_name, count(*) as count_first_name"), 
        Conditions("first_name = ? or first_name = ?", "Jan", "Piet"),
        Group("first_name"), 
        Order("first_name desc"))
    
    assertEquals(2, ps.size)
    assertEquals("Piet", ps(0).first_name)
    assertEquals(3, ps(0).count_first_name)
    assertEquals("Jan", ps(1).first_name)
    assertEquals(2, ps(1).count_first_name)
  }

  @Test
  def testSelectAllPeople_GroupByName_Having {
    val ps = Person.findAll(
        Select("first_name, count(*) as count_first_name"), 
        Group("first_name"), 
        Having("count(*) > 2"),
        Order("first_name desc"))
    
    assertEquals(1, ps.size)
    assertEquals("Piet", ps(0).first_name)
    assertEquals(3, ps(0).count_first_name)
  }

  @Test
  def testSelectFirst {
    val p: Option[Person] = Person.findFirst()
    
    assertTrue(p.isDefined)
  }

  @Test
  def testSelectLast {
    val p: Option[Person] = Person.findFirst(Order("id desc"))
    
    assertEquals(5, p.get.id)
  }
  
}
