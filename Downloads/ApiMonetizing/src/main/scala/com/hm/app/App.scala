package com.hm.app

import com.hm.ServerServiceActor
import com.hm.config.Configuration
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout

import spray.can.Http
/**
  * Created by swathi on 24/2/17.
  */
object App extends App with Configuration{



  implicit  val system=ActorSystem("on-spray-can")

  val service=system.actorOf(Props[ServerServiceActor],"ApiMonetizing")
  implicit  val timeout=Timeout(5)
  IO(Http) ! Http.Bind(service, serviceHost, servicePort)

}
