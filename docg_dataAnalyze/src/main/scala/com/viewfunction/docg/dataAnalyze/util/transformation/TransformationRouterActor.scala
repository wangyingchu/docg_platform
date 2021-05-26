package com.viewfunction.docg.dataAnalyze.util.transformation

import akka.actor.Actor

class TransformationRouterActor extends Actor {
  def receive = {
    case msg: String =>
      println(s"RemoteActor received message '$msg'")
      println(sender)
      //sender ! "Hello2 from the RemoteActor"
      //implicit val timeout = Timeout(10.seconds)
      sender.tell("this is return from RemoteActor",self)
  }
}

