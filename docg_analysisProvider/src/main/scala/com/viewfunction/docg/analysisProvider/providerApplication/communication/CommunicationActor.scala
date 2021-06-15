package com.viewfunction.docg.analysisProvider.providerApplication.communication

import akka.actor.Actor

class CommunicationActor(communicationMessageHandler:CommunicationMessageHandler) extends Actor {
  def receive = {
    case msg :Any =>
      communicationMessageHandler.handleMessage(msg,self,sender)
  }
}

