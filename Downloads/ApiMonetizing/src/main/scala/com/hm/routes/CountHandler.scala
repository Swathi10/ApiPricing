package com.hm.routes


import java.sql.Date

import spray.json._
import com.hm.connector.MySqlClient
import spray.routing.HttpService

import scala.collection.mutable.HashMap
import scala.collection.mutable
import java.sql.Timestamp


/**
  * Created by swathi on 24/2/17.
  */

object CountHandler {

  val timeMap: mutable.HashMap[Timestamp, mutable.HashMap[String,Integer]] = {
    new HashMap[Timestamp, mutable.HashMap[String,Integer]]
  }
  val counterMap: mutable.HashMap[String, Integer] = {
    new HashMap[String, Integer]
  }
  val counterMap1: mutable.HashMap[String, Integer] = {
    new HashMap[String, Integer]
  }

  def updateCounter(userName: String,time: Timestamp) = {

    timeMap.get(time) match{
      case Some(counterMap) =>
        {
          counterMap.get(userName) match{
            case Some(count) => {counterMap.put(userName,count+1)
              timeMap.put(time,counterMap)}
            case None => {counterMap.put(userName, 1)
              timeMap.put(time,counterMap)}
        }
        }
      case None => {counterMap.put(userName,1)
        timeMap.put(time,counterMap)}
    }



//    counterMap.get(userName) match {
//      case Some(count) => counterMap.put(userName, count + 1)
//      case None => counterMap.put(userName, 1)
    }


  def updateCounter1(userName: String) = {
    counterMap1.get(userName) match {
      case Some(count) => counterMap1.put(userName, count + 1)
      case None => counterMap1.put(userName, 1)
    }

  }
def timeRound(timestamp: Long) = {
  var timestampval= (timestamp - (timestamp % (10 * 1000 * 60) ) )
val timestamp1 = new java.sql.Timestamp(timestampval);

  println(timestamp1)
  timestamp1
}
}


trait CountHandler extends HttpService with AuthenticationHandler {

  def countByProductLine = post {


    entity(as[String]) {

      body => {

        val json = body.parseJson.asJsObject
        val prodLine = json.getFields("ProductLine").head.asInstanceOf[JsString].value

        optionalCookie("userName") {
          case Some(cookie) => {
            val userName = cookie.content
            val value = countProdLineApi(prodLine, userName)


            complete("count successful. Count = " + value)

          }
          case None => complete("no cookie")
        }


      }
    }

  }

  def countByCustomer = post {
    entity(as[String]) {
      body => {
        val json = body.parseJson.asJsObject
        val customer = json.getFields("customerID").head.asInstanceOf[JsString].value

        optionalCookie("userName") {
          case Some(cookie) => {
            val userName = cookie.content
            val value = countCustomerApi(customer, userName)
            //          if (status) {

            complete("count successful. Count = " + value)
            //          } else
            //          {
            //            complete("count failed")
            //          }
          }
          case None => complete("no cookie")
        }

      }
    }

  }

  def countProdLineApi(prodLine: String, userName: String) = {
    //    var mb = 1024 * 1024;
    //    val t0 = System.nanoTime()

    val rs = MySqlClient.getResultSet("select count(ProductCode) as total from products where ProductLine ='" + prodLine + "'")
  val time=CountHandler.timeRound(System.currentTimeMillis())
    CountHandler.updateCounter(userName,time)

    //println(CountHandler.count)

    var value = 0

    if (rs.next()) {
      value = rs.getInt("total")

    }

    value
  }

  def countCustomerApi(prodLine: String, userName: String) = {
    //    var mb = 1024*1024;
    //    val t0 = System.currentTimeMillis()
    CountHandler.updateCounter1(userName)
    val rs = MySqlClient.getResultSet("select count(productCode) as total from orderdetails where orderNumber in (select orderNumber from orders where customerNumber= " + prodLine + " AND status = 'Shipped')")


    var value = 0
    if (rs.next()) {
      value = rs.getInt("total")

    }

    value
  }

  //  val system=ActorSystem("on-spray-can")
  //system.scheduler.schedule(10 seconds, 10 seconds) {
  //  def updatevisitsproduct(username: String, count: Int): Boolean = {
  //
  //
  //    MySqlClient.statement.setInt(1, count)
  //    MySqlClient.statement.setString(2, username)
  //   // MySqlClient.statement1.setString(1, username)
  //    MySqlClient.statement.addBatch()
  //    //MySqlClient.statement1.addBatch()
  //
  //
  //    true
  //  }
  //  def updatevisitscust(username: String, count: Int): Boolean = {
  //
  //
  //    MySqlClient.statement1.setInt(1, count)
  //    //MySqlClient.statement.setString(2, username)
  //    MySqlClient.statement1.setString(2, username)
  //    //MySqlClient.statement.addBatch()
  //    MySqlClient.statement1.addBatch()
  //
  //
  //    true
  //  }
}