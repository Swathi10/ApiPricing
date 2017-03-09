package com.hm.connector

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by hari on 17/2/17.
  */
object MysqlClient {

  private val dbc = "jdbc:mysql://" + "127.0.0.1" + ":" + 3306 + "/" + "mysql" + "?user=" + "root" + "&password=" + "root"
  classOf[com.mysql.jdbc.Driver]
  private var conn: Connection = DriverManager.getConnection(dbc)

  def getConnection: Connection = {
    if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
    }
    conn
  }

  val autoIncValuesForTable: Map[String, Array[String]] = Map(
    "grp" -> Array("id")

  )

  def closeConnection() = conn.close()

  def executeQuery(query: String): Boolean = {
    val statement = getConnection.createStatement()
    try
      statement.execute(query)
    finally statement.close()
  }

  def getResultSet(query: String): ResultSet={
        val statement=getConnection.createStatement()
        statement.executeQuery(query)
      }
}