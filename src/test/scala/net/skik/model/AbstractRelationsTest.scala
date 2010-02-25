package net.skik.model

import org.junit.Before
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import net.skik.model._
import net.skik.sample.model._

//TODO invoices, invoice_items
abstract class AbstractRelationsTest {

  @Before
  def setUp {
    Base.execQuery(new SqlQuery("truncate table products"))
    Base.execQuery(new SqlQuery("truncate table orders"))
    Base.execQuery(new SqlQuery("truncate table line_items"))
    Base.execQuery(new SqlQuery("truncate table categories"))
    Base.execQuery(new SqlQuery("truncate table categories_products"))
    insertFixture
  }

  def insertInto(baseObject: BaseObject[_], values: (Symbol, Any)*) {
    val query = new StringBuffer("insert into ")
    query.append(baseObject.tableName)
    query.append(" (")
    query.append(values.map(kv => kv._1.name).mkString(","))
    query.append(") values (")
    query.append(values.toList.map { kv => kv._2 match {
      case s: String => "'" + s + "'"
      case null => "null"
      case s => s.toString
    }}.mkString(","))
    query.append(")")
    Base.execQuery(new SqlQuery(query.toString))
  }

  def insertFixture: Unit
}

object AbstractRelationsTest {
  
  @BeforeClass
  def beforeClass {
    Base.establishConnection(MySqlAdapter, host = "localhost", database = "inschr_rer", username = "root", password = "")
    Base.execQuery(new SqlQuery("drop table if exists products"))
    Base.execQuery(new SqlQuery("drop table if exists orders"))
    Base.execQuery(new SqlQuery("drop table if exists line_items"))
    Base.execQuery(new SqlQuery("drop table if exists categories"))
    Base.execQuery(new SqlQuery("drop table if exists categories_products"))
    Base.execQuery(new SqlQuery(
      """create table products
      ( id int auto_increment
      , title varchar(50)
      , description varchar(250)
      , image_url varchar(80)
      , price decimal(8,2)
      , primary key(id)
      )"""))
    Base.execQuery(new SqlQuery(
      """create table orders
      ( id int auto_increment
      , name varchar(50)
      , address varchar(250)
      , email varchar(80)
      , pay_type varchar(10)
      , primary key(id)
      )"""))
    Base.execQuery(new SqlQuery(
      """create table line_items
      ( id int auto_increment
      , product_id int not null
      , order_id int not null
      , quantity int not null
      , total_price decimal(8,2)
      , primary key(id)
      )"""))
    Base.execQuery(new SqlQuery(
      """create table categories
      ( id int auto_increment
      , name varchar(50)
      , primary key(id)
      )"""))
    Base.execQuery(new SqlQuery(
      """create table categories_products
      ( product_id int not null
      , category_id int not null
      , primary key(product_id, category_id)
      )"""))
  }
  
  @AfterClass
  def afterClass {
    Base.execQuery(new SqlQuery("drop table if exists products"))
    Base.execQuery(new SqlQuery("drop table if exists orders"))
    Base.execQuery(new SqlQuery("drop table if exists line_items"))
    Base.execQuery(new SqlQuery("drop table if exists categories"))
    Base.execQuery(new SqlQuery("drop table if exists categories_products"))
    Base.connection.close
  }
}
