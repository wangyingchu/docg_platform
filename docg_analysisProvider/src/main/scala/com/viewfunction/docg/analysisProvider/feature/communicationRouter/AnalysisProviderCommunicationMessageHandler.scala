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
import java.time.ZoneId
import java.time.Instant

class AnalysisProviderCommunicationMessageHandler(globalDataAccessor :GlobalDataAccessor,internalOperationDB:InternalOperationDB) extends CommunicationMessageHandler{
  override def handleMessage(communicationMessage: Any, communicationActor: ActorRef, senderActor: ActorRef): Unit = {

    var analyseResponse:AnalyseResponse=null

    communicationMessage match {
      case communicationMessage:AnalyseRequest =>
        analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.setResponseDataForm(communicationMessage.getResponseDataForm)
        val currentTime = LocalDateTime.now()
        println("################################################################")
        println("Service Analysis: "+communicationMessage.getRequestUUID + " at: " + currentTime + "")
        println("################################################################")

        val timestamp: Long = communicationMessage.getRequestDateTime
        val instant: Instant = Instant.ofEpochMilli(timestamp)
        val requestLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        internalOperationDB.recordFeatureRequest(communicationMessage.getRequestUUID,requestLocalDateTime,analyseResponse.getResponseUUID,communicationMessage.getResponseDataForm.toString,currentTime)
    }
    if(analyseResponse!=null){
      communicationMessage match {
        case communicationMessage: String =>
          println(s" $communicationMessage")


        case communicationMessage: SpatialPropertiesAggregateStatisticRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "SpatialPropertiesAggregateStatistic")
          SpatialPropertiesStatisticAndAnalysis.doExecuteSpatialPropertiesAggregateStatistic(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest])

        case communicationMessage: AdministrativeDivisionSpatialCalculateRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "AdministrativeDivisionSpatialCalculate")
          AdministrativeDivisionBasedSpatialAnalysis.doExecuteDataSliceAdministrativeDivisionSpatialCalculation(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest])
      }
    }

    if(analyseResponse!=null){
      val responseDatetime = new Date
      val requestUUID = analyseResponse.getRequestUUID
      analyseResponse.setResponseDateTime(responseDatetime)
      val instant: Instant = responseDatetime.toInstant
      val localDateTime: LocalDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime
      internalOperationDB.recordFeatureResponse(requestUUID, localDateTime)
      println("################################################################")
      println("Response Analysis: "+requestUUID+ " at: " + localDateTime + "")
      println("################################################################")
      senderActor.tell(analyseResponse,communicationActor)
    }
  }
}
