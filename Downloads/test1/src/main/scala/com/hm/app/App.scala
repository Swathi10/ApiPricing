package com.hm.app

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.hm.ServerServiceActor
//import com.hm.app.App.{serviceHost, servicePort}
import com.hm.config.Configuration
import spray.can.Http

object App extends App with Configuration{



  implicit  val system=ActorSystem("on-spray-can")

  val service=system.actorOf(Props[ServerServiceActor],"EditCollection")
  implicit  val timeout=Timeout(5)
  IO(Http) ! Http.Bind(service, serviceHost, servicePort)
}
