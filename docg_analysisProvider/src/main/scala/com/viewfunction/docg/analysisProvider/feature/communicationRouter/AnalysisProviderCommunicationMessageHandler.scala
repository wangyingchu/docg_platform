package com.viewfunction.docg.analysisProvider.feature.communicationRouter

import akka.actor.ActorRef
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse}
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationMessageHandler
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialPropertiesAggregateStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.{AdministrativeDivisionBasedSpatialAnalysis, SpatialPropertiesStatisticAndAnalysis}
import com.viewfunction.docg.analysisProvider.providerApplication.util.InternalOperationDB

import java.time.LocalDateTime
import java.util.Date

class AnalysisProviderCommunicationMessageHandler(globalDataAccessor :GlobalDataAccessor,internalOperationDB:InternalOperationDB) extends CommunicationMessageHandler{
  override def handleMessage(communicationMessage: Any, communicationActor: ActorRef, senderActor: ActorRef): Unit = {

    var analyseResponse:AnalyseResponse=null

    communicationMessage match {
      case communicationMessage:AnalyseRequest =>
        analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.setResponseDataForm(communicationMessage.getResponseDataForm)
        //val serviceDatetime = new Date
        val currentTime = LocalDateTime.now()
        println("################################################################")
        println("Service Analysis: "+communicationMessage.getRequestUUID + " at: " + currentTime + "")
        println("################################################################")

        internalOperationDB.recordFeatureRequest(communicationMessage.getRequestUUID,analyseResponse.getResponseUUID,communicationMessage.getResponseDataForm.toString,currentTime)
    }
    if(analyseResponse!=null){
      communicationMessage match {
        case communicationMessage: String =>
          println(s" $communicationMessage")


        case communicationMessage: SpatialPropertiesAggregateStatisticRequest =>
          SpatialPropertiesStatisticAndAnalysis.doExecuteSpatialPropertiesAggregateStatistic(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest])

        case communicationMessage: AdministrativeDivisionSpatialCalculateRequest =>
          AdministrativeDivisionBasedSpatialAnalysis.doExecuteDataSliceAdministrativeDivisionSpatialCalculation(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest])
      }
    }

    if(analyseResponse!=null){
      val responseDatetime = new Date
      val requestUUID = analyseResponse.getRequestUUID
      analyseResponse.setResponseDateTime(responseDatetime)
      println("################################################################")
      println("Response Analysis: "+requestUUID+ " at: " + responseDatetime + "")
      println("################################################################")
      senderActor.tell(analyseResponse,communicationActor)
    }
  }
}
