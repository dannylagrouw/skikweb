package net.skik.model

import java.sql.Connection
import java.sql.DriverManager

object MySqlAdapter extends Adapter {

  override def establishConnection(host: String, database: String, username: String, password: String): Connection = {
    Class.forName("com.mysql.jdbc.Driver")
    val url = "jdbc:mysql://" + host + ":3306/" + database
    DriverManager.getConnection(url, username, password)
  }

  override def createQuery(mode: QueryMode.Value) = new MySqlQuery(mode)

}
