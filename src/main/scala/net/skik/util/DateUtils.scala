package net.skik.util

import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat

object DateUtils {

  val DATE_FORMAT_YMD = "yyyyMMdd"
  val DATE_FORMAT_YMDHMS = "yyyyMMddHHmmss"
  
  implicit def TimeUnitWrapper(number: Int) = new TimeUnit(number)
  implicit def StringToDate(dateStr: String) = parse(dateStr)
  implicit def TimeFormatterWrapper(date: Date) = new TimeFormatter(date)

  def parse(dateStr: String): Date = {
    if (dateStr != null && dateStr.length == 8)
      new SimpleDateFormat(DATE_FORMAT_YMD).parse(dateStr)
    else
      new SimpleDateFormat(DATE_FORMAT_YMDHMS).parse(dateStr)
  }
  
}

class TimeFormatter(val date: Date) {
  def formatted = new SimpleDateFormat(DateUtils.DATE_FORMAT_YMDHMS).format(date)
  def formatted(format: String) = new SimpleDateFormat(format).format(date)
}

class TimeUnit(val number: Int) {
  
  var field: Int = _
  var fromDate: Option[Date] = None
  
  def from(d: Date) = {
    fromDate = Some(d)
    this
  }
  
  def days = {
    field = Calendar.DAY_OF_MONTH
    this
  }
  
  def months = {
    field = Calendar.MONTH
    this
  }
  
  def years = {
    field = Calendar.YEAR
    this
  }
  
  def ago = {
    val c = Calendar.getInstance
    if (fromDate.isDefined)
      c.setTime(fromDate.get)
    c.add(field, -number)
    c.getTime
  }
  
}

