package com.viewfunction.docg.dataAnalyze.feature.transformation

import akka.actor.ActorRef
import com.viewfunction.docg.dataAnalyze.util.transformation.TransformationMessageHandler

class DefaultTransformationMessageHandler extends TransformationMessageHandler{
  override def handleTransformationMessage(transformationMessage: Any, transformationRouterActor: ActorRef,senderActor:ActorRef): Unit = {

    transformationMessage match {
      case transformationMessage: String =>
        println(s"DefaultTransformationMessageHandler received message '$transformationMessage'")
        println(senderActor)
        //sender ! "Hello2 from the RemoteActor"
        //implicit val timeout = Timeout(10.seconds)
        senderActor.tell("this is return from RemoteActor", transformationRouterActor)
    }
  }
}
