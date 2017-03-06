package com.hm.connector

/**
  * Created by swathi on 24/2/17.
  */


import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import akka.actor.ActorSystem
import com.hm.routes.CountHandler

import scala.collection.mutable.ArrayBuffer

object MySqlClient {

  private val dbc = "jdbc:mysql://" + "127.0.0.1" + ":" + 3306 + "/" + "classicmodels" + "?user=" + "root" + "&password=" + "root"
  classOf[com.mysql.jdbc.Driver]
  private var conn: Connection = DriverManager.getConnection(dbc)

  def getConnection: Connection = {
    if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
    }
    conn
  }

  val autoIncValuesForTable: Map[String, Array[String]] = Map(
    "request_header" -> Array("id")

  )

  def closeConnection() = conn.close()

// val statement=MySqlClient.getConnection.prepareStatement(" update pricing set papivisits=papivisits+(?) where name = (?)")
//
//  val statement1=MySqlClient.getConnection.prepareStatement(" update pricing set capivisits=capivisits+(?) where name = (?)")
  val statement2=MySqlClient.getConnection.prepareStatement("insert into user(name,user_name,password) values (?,?,?)")
  val statement3=MySqlClient.getConnection.prepareStatement("insert into pricing(name,capivisits,papivisits) values (?,0,0)")
    def executeQuery(query: String): Boolean = {
    val statement = getConnection.createStatement()
    try
      statement.execute(query)
    finally statement.close()
  }

  def getResultSet(query: String): ResultSet = {
    val statement = getConnection.createStatement()
    statement.executeQuery(query)
  }
  def executeUpdate(query: String): Int = {
    val statement = getConnection.createStatement()
    statement.executeUpdate(query)
  }

  def insert(tableName: String, elements: Map[String, Any]): Int = {
    try {
      val colNames: ArrayBuffer[String] = ArrayBuffer()
      val values: ArrayBuffer[Any] = ArrayBuffer()
      elements.foreach(i => {
        colNames += i._1
        values += i._2
      })

      val insertQuery = "INSERT INTO " + tableName + " (" + colNames.mkString(",") + ") VALUES (" + colNames.indices.map(i => "?").mkString(",") + ")"

      val returnColumns: Array[String] = autoIncValuesForTable.getOrElse(tableName, Array())
      val preparedStatement: PreparedStatement = getConnection.prepareStatement(insertQuery, returnColumns)

      values.zipWithIndex.foreach(i => addToPreparedStatement(i._1, i._2 + 1, preparedStatement))
      var generatedId: Int = 0
      try {

        preparedStatement.executeUpdate()
        if (returnColumns.nonEmpty) {
          val gkSet = preparedStatement.getGeneratedKeys
          if (gkSet.next()) {
            generatedId = gkSet.getInt(1)
          }
        }
      }
      finally preparedStatement.close()

      generatedId
    } catch {
      case e: Exception => e.printStackTrace()
        0
    }
  }

  private def addToPreparedStatement(value: Any, index: Int, preparedStatement: PreparedStatement) = {
    value match {
      case v: Long => preparedStatement.setLong(index, v)
      case v: Int => preparedStatement.setInt(index, v)
      case v: Double => preparedStatement.setDouble(index, v)
      case v: String => preparedStatement.setString(index, v)

      case v: Array[Byte] => preparedStatement.setBytes(index, v)
      case v: Serializable => preparedStatement.setObject(index, v)
      case _ => preparedStatement.setString(index, value.toString)
    }
  }

  import system.dispatcher
  import scala.concurrent.duration._
  // ...now with system in current scope:
  val system=ActorSystem("on-spray-can")
  system.scheduler.schedule(11 seconds, 11 seconds) {

CountHandler.counterMap.foreach(i=>{
  println("User "+i._1+" Count "+i._2)
  if(i._2!=0) {
    val rs = MySqlClient.getResultSet("select * from pricing where name='" + i._1 + "'")
    if (rs.next()) {
      val query = MySqlClient.executeQuery(" update pricing set papivisits=papivisits+" + i._2 + " where name ='" + i._1 + "'")
    }
    else {
      MySqlClient.executeQuery("insert into pricing values('" + i._1 + "',0," + i._2 + ")")

    }
    rs.close()
    CountHandler.counterMap.put(i._1, 0)
  }
})




    CountHandler.counterMap1.foreach(i=>{
      if(i._2!=0) {
        val rs = MySqlClient.getResultSet("select * from pricing where name='" + i._1 + "'")
        if (rs.next()) {
          val query = MySqlClient.executeQuery(" update pricing set  capivisits=capivisits+" + i._2 + " where name ='" + i._1 + "'")
        }
        else {
          MySqlClient.executeQuery("insert into pricing values('" + i._1 + "'," + i._2 + ",0)")
        }
        rs.close()
        CountHandler.counterMap1.put(i._1, 0)
      }
    })



  }

}


