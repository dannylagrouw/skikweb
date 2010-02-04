package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

/**
 * Assumes a resultset filled with any value in the
 * first column and a number in the second. Maps these
 * to a tuple (Any, Number).
 */
class GroupedValueMapper[A] extends Mapper[A] {

  override def map(rs: ResultSet) =
    (rs.getObject(1), rs.getObject(2)).asInstanceOf[A]
  
}
