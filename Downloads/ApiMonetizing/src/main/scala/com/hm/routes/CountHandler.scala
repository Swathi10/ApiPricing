package com.hm.routes


import com.hm.connector.MySqlClient
import spray.routing.HttpService
import spray.json.{JsArray, JsNumber, JsObject, JsString, _}

import scala.runtime

/**
  * Created by swathi on 24/2/17.
  */
trait CountHandler extends HttpService {

  def countByProductLine = post {
    val t0 = System.nanoTime()

    entity(as[String]) {

      body => {
        val json = body.parseJson.asJsObject
        val prodLine = json.getFields("ProductLine").head.asInstanceOf[JsString].value
        val (status, value) = countProdLineApi(prodLine)
          if (status) {

          complete("count successful. Count = "+value)


        }
        else {
          complete("count failed")
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
        val (status, value) = countCustomerApi(customer)
        if (status) {
          complete("count successful. Count = "+value)

        }
        else {
          complete("count failed")
        }
      }
    }

  }

  def countProdLineApi(prodLine: String) = {
    var mb = 1024*1024;
    val t0 = System.nanoTime()

    val rs = MySqlClient.getResultSet("select count(ProductCode) as total from products where ProductLine ='" + prodLine + "'")
    val t1 = System.nanoTime()
    println(t1-t0)
    var value = 0
    var status = false
    if (rs.next()) {
      value = rs.getInt("total")
      status = true
    }
    var runtime = Runtime.getRuntime();

    System.out.println("##### Heap utilization statistics [MB] #####");
    System.out.println("Used Memory for productlineapi:"
      + (runtime.totalMemory() - runtime.freeMemory()) / mb);
    (status,value)
  }

  def countCustomerApi(prodLine: String) = {
    var mb = 1024*1024;
    val t0 = System.nanoTime()
    val rs = MySqlClient.getResultSet("select count(productCode) as total from orderdetails where orderNumber in (select orderNumber from orders where customerNumber= " + prodLine + " AND status = 'Shipped')")
    val t1 = System.nanoTime()
    println(t1-t0)
    var status = false
    var value = 0
    if (rs.next()) {
      value = rs.getInt("total")
      status = true
    }
    var runtime = Runtime.getRuntime();

    System.out.println("##### Heap utilization statistics [MB] #####");
    System.out.println("Used Memory for customerapi:"
      + (runtime.totalMemory() - runtime.freeMemory()) / mb);
    (status,value)
  }


}