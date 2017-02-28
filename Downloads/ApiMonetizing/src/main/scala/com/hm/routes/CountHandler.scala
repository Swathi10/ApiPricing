package com.hm.routes


import com.hm.connector.MySqlClient
import spray.can.Http
import spray.http.HttpCookie
import spray.routing.HttpService
import spray.json.{JsArray, JsNumber, JsObject, JsString, _}

import scala.runtime

/**
  * Created by swathi on 24/2/17.
  */
trait CountHandler extends HttpService with AuthenticationHandler {

  def countByProductLine = post {
    // val t0 = System.nanoTime()

    entity(as[String]) {

      body => {

        val json = body.parseJson.asJsObject
        val prodLine = json.getFields("ProductLine").head.asInstanceOf[JsString].value

        optionalCookie("userName") {
          case Some(cookie) => {
            val value = countProdLineApi(prodLine, cookie.content)
            //            if (status) {

            complete("count successful. Count = " + value)
            //            }else {
            //              complete("count failed")
            //            }
          }
          case None => complete("no cookie")
        }


      }
    }
    //print(t1-t0)
  }

  def countByCustomer = post {
    entity(as[String]) {
      body => {
        val json = body.parseJson.asJsObject
        val customer = json.getFields("customerID").head.asInstanceOf[JsString].value

        optionalCookie("userName") {
          case Some(cookie) => {
            val value = countCustomerApi(customer, cookie.content)
            //            if (status) {

            complete("count successful. Count = " + value)
            //            }else {
            //              complete("count failed")
            //            }
          }
          case None => complete("no cookie")
        }

      }
    }

  }

  def countProdLineApi(prodLine: String, username: String) = {
    //    var mb = 1024 * 1024;
    //    val t0 = System.nanoTime()

    val rs = MySqlClient.getResultSet("select count(ProductCode) as total from products where ProductLine ='" + prodLine + "'")
   updatevisits(username)

   // val query =
    //    println(query)
   // val rs1 = MySqlClient.executeUpdate(query)
    //    val t1 = System.nanoTime()
    //    println(t1 - t0)
    var value = 0
    //  var status = false
    if (rs.next()) {
      value = rs.getInt("total")
      //   status = true
    }
    //    var runtime = Runtime.getRuntime();
    //
    //    System.out.println("##### Heap utilization statistics [MB] #####")
    //    System.out.println("Used Memory for productlineapi:"
    //      + (runtime.totalMemory() - runtime.freeMemory()) / mb)
    value
  }

  def countCustomerApi(prodLine: String, username: String) = {
    //    var mb = 1024*1024;
    //    val t0 = System.currentTimeMillis()
    val rs = MySqlClient.getResultSet("select count(productCode) as total from orderdetails where orderNumber in (select orderNumber from orders where customerNumber= " + prodLine + " AND status = 'Shipped')")

    updatevisits(username)//val query = "update pricing set capivisits=capivisits+1 where name = '" + username + "'"
    //    println(query)
     // val rs1 = MySqlClient.executeUpdate(query)
    //    val t1 = System.currentTimeMillis()
    //println(t1-t0)
    //    var status = false
    var value = 0
    if (rs.next()) {
      value = rs.getInt("total")
      //      status = true
    }
    //    var runtime = Runtime.getRuntime();

    //
    value
  }

  def updatevisits(username: String):Boolean={


    MySqlClient.statement.setString(1,username)

    MySqlClient.statement1.setString(1,username)
    MySqlClient.statement.addBatch()
    MySqlClient.statement1.addBatch()


    true
  }
}