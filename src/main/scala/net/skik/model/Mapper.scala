package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

trait Mapper {

  var metaData: ResultSetMetaData = _
  
  def map(rs: ResultSet, n: Int): Any
  
}
