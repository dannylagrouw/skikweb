package net.skik.model

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.apache.commons.beanutils._
import net.skik.util.LangUtils._

class ValueMapper[A] extends Mapper[A] {

  override def map(rs: ResultSet) = {
    rs.getObject(1).asInstanceOf[A]
  }
}
