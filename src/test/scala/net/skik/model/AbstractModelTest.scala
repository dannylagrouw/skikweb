package net.skik.model

import org.junit.Before
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

abstract class AbstractModelTest {

  var lastId = 1

  @Before
  def setUp {
    Base.execQuery(new SqlQuery("truncate table people"))
    lastId = 1
    insertFixture
  }

  def insertPerson(first: String, last: String, nr: Long) {
    Base.execQuery(new SqlQuery("insert into people (id, first_name, last_name, nr) values (" + lastId + ", '" + first + "', '" + last + "'," + nr + ")"))
    lastId += 1
  }

  def insertFixture: Unit
}

object AbstractModelTest {
  
  @BeforeClass
  def beforeClass {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("drop table if exists people"))
    Base.execQuery(new SqlQuery("""create table people
      ( id int auto_increment
      , first_name varchar(50)
      , last_name varchar(80) not null
      , nr int
      , opt_in tinyint(1)
      , opt_in_on date
      , primary key(id)
      )"""))
  }
  
  @AfterClass
  def afterClass {
    Base.execQuery(new SqlQuery("drop table if exists people"))
    Base.connection.close
  }
}
