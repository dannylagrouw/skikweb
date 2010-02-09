package net.skik.util

import java.text.ParseException
import java.util.Calendar
import java.util.Calendar._
import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import DateUtils._

class DateUtilsTest {

  @Test(expected = classOf[NullPointerException])
  def testParse_Null {
    parse(null)
  }

  @Test(expected = classOf[ParseException])
  def testParse_Empty {
    parse("")
  }
  
  @Test
  def testParse_DateTime {
    val c = Calendar.getInstance
    c.set(DAY_OF_MONTH, 31)
    c.set(MONTH, DECEMBER)
    c.set(YEAR, 2010)
    c.set(HOUR_OF_DAY, 22)
    c.set(MINUTE, 55)
    c.set(SECOND, 30)
    c.set(MILLISECOND, 0)
    assertEquals(c.getTime, parse("20101231225530"))
  }
  
  @Test
  def testDaysAgo {
    assertEquals("20100202000000", 10.days.from("20100212").ago.formatted)
  }
  
  @Test
  def testMonthsAgo {
    assertEquals("20100202000000", 10.months.from("20101202").ago.formatted)
  }
  
}
