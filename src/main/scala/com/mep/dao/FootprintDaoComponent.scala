package com.mep.dao


import java.sql.{Connection, DriverManager}

class FootprintDaoComponent {
  def footprintDao: FootprintDao = new FootprintDao

  class FootprintDao {
    val url = "jdbc:mysql://localhost:3306/envfootprint?useSSL=false"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = "root"
    var connection: Connection = _

    def getFootprintById(userId: Int): Int = {
      var footprint: Int = -1
      try {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        val statement = connection.createStatement
        val rs = statement.executeQuery(s"SELECT amount FROM footprint where userId = $userId")
        while (rs.next) {
          footprint = rs.getInt("amount")
        }
      } catch {
        case e: Exception => e.printStackTrace
      }
      connection.close
      footprint
    }

    def printAllFootprints(userId: Int): Unit = {
      var allAmounts = Seq[Int]()
      try {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        val statement = connection.createStatement
        val rs = statement.executeQuery("SELECT userId, amount FROM footprint")
        while (rs.next) {
          val id = rs.getInt("userId")
          val amount = rs.getInt("amount")
          allAmounts = allAmounts :+ amount
          println("title = %s, author = %s".format(id,amount))
        }
      } catch {
        case e: Exception => e.printStackTrace
      }
      connection.close
      allAmounts.headOption.getOrElse(-1)
    }
  }
}
