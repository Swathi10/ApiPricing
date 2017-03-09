package com.hm

import akka.actor.Actor
import com.hm.routes.Routes

import scala.concurrent.ExecutionContext


/**
  * Created by hari on 17/2/17.
  */
class ServerServiceActor  extends Actor with Routes {

  //Akka Actor Context is set as the actorRefFactory
  def actorRefFactory = context


  implicit def dispatcher: ExecutionContext = ServerActorSystem.ec

  override def receive: Receive =runRoute(route)

}
