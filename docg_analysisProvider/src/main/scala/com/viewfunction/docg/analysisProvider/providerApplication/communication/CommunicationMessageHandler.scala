package com.viewfunction.docg.analysisProvider.providerApplication.communication

import akka.actor.ActorRef

abstract class CommunicationMessageHandler {
  def handleMessage(communicationMessage:Any,communicationActor:ActorRef,senderActor:ActorRef):Unit
}
