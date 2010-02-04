package net.skik.sample.model

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

trait AbstractModelTest {

  var lastId = 1

  @Before
  def setUp: Unit = {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("delete from people"))
    lastId = 1
    insertFixture
  }

  def insertPerson(first: String, last: String, nr: Long): Unit = {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, last_name, nr) values (" + lastId + ", '" + first + "', '" + last + "'," + nr + ")"))
    lastId += 1
  }

  def insertFixture: Unit
}
