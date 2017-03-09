package com.hm.routes

import spray.routing.HttpService

import scala.collection.mutable.ArrayBuffer

/**
  * Created by swathi on 6/3/17.
  */
trait Routes extends UserHandler with HttpService{
   val route =
    path("add") {

      add
     // complete("done")

    } ~ path("del") {
      del
      //complete("done")
    }~path("test") {
      test
      //complete("done")
    }
}