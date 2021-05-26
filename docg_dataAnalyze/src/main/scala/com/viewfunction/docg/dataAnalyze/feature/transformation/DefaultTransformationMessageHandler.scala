package com.viewfunction.docg.dataAnalyze.feature.transformation

import akka.actor.ActorRef
import com.viewfunction.docg.dataAnalyze.feature.transformation.messagePayload.AnalyzeTreesCrownAreaInSection
import com.viewfunction.docg.dataAnalyze.util.transformation.TransformationMessageHandler

class DefaultTransformationMessageHandler extends TransformationMessageHandler{
  override def handleTransformationMessage(transformationMessage: Any, transformationRouterActor: ActorRef,senderActor:ActorRef): Unit = {

    transformationMessage match {
      case transformationMessage: String =>
        println(s"DefaultTransformationMessageHandler received message '$transformationMessage'")
        println(senderActor)
        senderActor.tell("this is return from RemoteActor", transformationRouterActor)
      case transformationMessage: AnalyzeTreesCrownAreaInSection =>
        println(transformationMessage.treeType+" "+transformationMessage.crownSize)
        senderActor.tell("AnalyzeTreesCrownAreaInSection Executed", transformationRouterActor)
    }
  }
}
