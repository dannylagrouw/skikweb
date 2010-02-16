package net.skik.util

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import java.util.Date
import ReflectionUtils._

class Dummy {
  var string: String = "abcdef"
  var i: Int = 123456
  var l: Long = 123456789012345l
  var c: Char = 'X'
  var b: Boolean = true
  var date: Date = new Date
  var bigdecimal = new java.math.BigDecimal("9" * 99)
  val readonly = true
}

object Dummy {
  var op = "object_property"
  def myClass = this.getClass
}

class ReflectionUtilsTest {

  var dummy = new Dummy
  
  def testPrint {
    println(dummy.getClass.getMethods.toList.sort(_.getName<_.getName).mkString("\n"))
  }
  
  def testDummy {
    println(property(dummy, "string"))
    println(property(dummy, "i"))
    println(property(dummy, "l"))
    println(property(dummy, "c"))
    println(property(dummy, "b"))
    println(property(dummy, "date"))
    println(property(dummy, "bigdecimal"))
  }

  @Test
  def testBaseClass {
    assertEquals(classOf[Dummy], baseClass(Dummy.myClass))
  }
  
  @Test
  def testObjectClass {
    assertEquals(Dummy.myClass, objectClass(classOf[Dummy]))
  }
  
  @Test
  def testObjectProperty {
    assertEquals(Dummy.op, objectProperty(classOf[Dummy], "op"))
  }
  
  @Test
  def testHasProperty {
    assertTrue(hasProperty(classOf[Dummy], "date"))
  }
  
  @Test
  def testHasProperty_Readonly {
    assertFalse(hasProperty(classOf[Dummy], "readonly"))
  }
  
  @Test
  def testHasWriteProperty {
    assertTrue(hasWriteProperty(classOf[Dummy], "date"))
  }
  
  @Test
  def testHasWriteProperty_Readonly {
    assertFalse(hasWriteProperty(classOf[Dummy], "readonly"))
  }
  
  @Test
  def testHasReadProperty {
    assertTrue(hasReadProperty(classOf[Dummy], "date"))
  }
  
  @Test
  def testHasReadProperty_Readonly {
    assertTrue(hasReadProperty(classOf[Dummy], "readonly"))
  }
  
  @Test
  def testProperty_String {
    assertEquals(dummy.string, property(dummy, "string"))
  }
  
  @Test
  def testProperty_Int {
    assertEquals(dummy.i, property(dummy, "i"): Int)
  }
  
  @Test
  def testProperty_Long {
    assertEquals(dummy.l, property(dummy, "l"): Long)
  }
  
  @Test
  def testProperty_Char {
    assertEquals(dummy.c, property(dummy, "c"): Char)
  }
  
  @Test
  def testProperty_Boolean {
    assertEquals(dummy.b, property(dummy, "b"))
  }
  
  @Test
  def testProperty_Date {
    assertEquals(dummy.date, property(dummy, "date"))
  }
  
  @Test
  def testProperty_BigDecimal {
    assertEquals(dummy.bigdecimal, property(dummy, "bigdecimal"))
  }
  

  
  @Test
  def testSetProperty_String {
    setProperty(dummy, "string", "xyzxyz")
    assertEquals("xyzxyz", dummy.string)
  }
  
  @Test
  def testSetProperty_Int {
    setProperty(dummy, "i", 98765)
    assertEquals(98765, dummy.i)
  }
  
  @Test
  def testSetProperty_Long {
    setProperty(dummy, "l", 9876543219876l)
    assertEquals(9876543219876l, dummy.l)
  }
  
  @Test
  def testSetProperty_Char {
    setProperty(dummy, "c", '!')
    assertEquals('!', dummy.c)
  }
  
  @Test
  def testSetProperty_Boolean {
    setProperty(dummy, "b", false)
    assertEquals(false, dummy.b)
  }
  
  @Test
  def testSetProperty_Date {
    val cal = java.util.Calendar.getInstance
    cal.add(java.util.Calendar.YEAR, -10)
    cal.add(java.util.Calendar.MONTH, -30)
    
    setProperty(dummy, "date", cal.getTime)
    assertEquals(cal.getTime, dummy.date)
  }
  
  @Test
  def testSetProperty_BigDecimal {
    val bd = BigDecimal("12345" * 8)
    
    setProperty(dummy, "bigdecimal", bd.bigDecimal)
    assertEquals(bd.bigDecimal, dummy.bigdecimal)
  }
  
}
