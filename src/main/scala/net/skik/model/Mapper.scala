package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

abstract class Mapper[T] {

  var metaData: ResultSetMetaData = _
  
  def map(rs: ResultSet, n: Int): T
  
}
