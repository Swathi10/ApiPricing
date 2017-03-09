package com.hm.routes

import com.hm.connector.MysqlClient
import spray.json.JsString
import spray.routing.HttpService
import spray.json._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by swathi on 6/3/17.
  */
trait UserHandler extends HttpService {

  def add = post {
    entity(as[String]) {
      body => {
        val json = body.parseJson.asJsObject
        val a = json.getFields("a").head.asInstanceOf[JsString].value
        println(a)
        val q = "Insert into list values (" + a + ")"
        val res = MysqlClient.executeQuery(q)

        complete("inserted")
      }
    }
  }

  def del = post {
    entity(as[String]) {
      body => {
        val json = body.parseJson.asJsObject
        val a = json.getFields("a").head.asInstanceOf[JsString].value

        val res = MysqlClient.executeQuery("Delete from list where numbers=(" + a + ")")
        complete("deleted")
      }
    }
  }


  def test = post {
    entity(as[String]) {
      body => {
        val json = body.parseJson.asJsObject
        val a = json.getFields("a").head.asInstanceOf[JsString].value.toInt
        val res = MysqlClient.getResultSet("Select * from list")
        var list = scala.collection.mutable.ArrayBuffer.empty[Int]
        while (res.next()) {
          list += res.getInt(1)
        }
        println(list)
        var size = list.size
        var maximum = list.max
        var minimum = list.min

        var temp = 0
        while (temp < size) {
          if (list(temp) > a && list(temp) < maximum) {
            maximum = list(temp)
          }
          temp = temp + 1
        }
        temp = 0
        while (temp < size) {
          if (list(temp) < a && list(temp) > minimum) {
            minimum = list(temp)
          }
          temp = temp + 1
        }
        println("maximum : " + maximum)
        println("minimum : " + minimum)
        complete("maximum : " + maximum+" \n minimum : " + minimum)
      }

    }


  }


}
