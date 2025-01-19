package com.viewfunction.docg.analysisProvider.feature.communicationRouter

import akka.actor.ActorRef
import com.viewfunction.docg.analysisProvider.feature.admin.AnalysisProviderAdministrationOperator
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse}
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationMessageHandler
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.{AnalysisProviderPingRequest, AnalysisProviderRegisterFunctionalFeatureRequest, AnalysisProviderRunningStatusRequest, AnalysisProviderUnregisterFunctionalFeatureRequest, FunctionalFeatureRunningStatusRequest, FunctionalFeaturesInfoRequest}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialPropertiesAggregateStatisticRequest, TemporalDurationBasedSpatialPropertiesStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.{AdministrativeDivisionBasedSpatialAnalysis, SpatialPropertiesStatisticAndAnalysis, TemporalDurationBasedSpatialPropertiesStatisticAnalysis}
import com.viewfunction.docg.analysisProvider.providerApplication.util.InternalOperationDB

import java.time.LocalDateTime
import java.util.Date
import java.time.ZoneId
import java.time.Instant

class AnalysisProviderCommunicationMessageHandler(globalDataAccessor :GlobalDataAccessor,internalOperationDB:InternalOperationDB) extends CommunicationMessageHandler{
  override def handleMessage(communicationMessage: Any, communicationActor: ActorRef, senderActor: ActorRef): Unit = {
    println()
    println("****************************************************************************************")
    println("Received Message: ")
    println(communicationMessage)

    var analyseResponse:AnalyseResponse=null

    communicationMessage match {
      case communicationMessage:AnalyseRequest =>
        analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.setResponseDataForm(communicationMessage.getResponseDataForm)
        val currentTime = LocalDateTime.now()
        println("----------------------------------------------------------------------------------------")
        println("Service Analysis: "+communicationMessage.getRequestUUID + " at: " + currentTime + "")

        val timestamp: Long = communicationMessage.getRequestDateTime
        val instant: Instant = Instant.ofEpochMilli(timestamp)
        val requestLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        internalOperationDB.recordFeatureRequest(communicationMessage.getRequestUUID,requestLocalDateTime,analyseResponse.getResponseUUID,communicationMessage.getResponseDataForm.toString,currentTime)
    }
    if(analyseResponse!=null){
      communicationMessage match {
        case communicationMessage: String =>
          println(s" $communicationMessage")

        //for provider administration
        case communicationMessage: AnalysisProviderPingRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "PingAnalysisProvider")
        case communicationMessage: FunctionalFeaturesInfoRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "ListFunctionalFeaturesInfo")
          AnalysisProviderAdministrationOperator.doProvideFunctionalFeaturesInfoList(internalOperationDB,analyseResponse)
        case communicationMessage: FunctionalFeatureRunningStatusRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "ListFunctionalFeatureRunningStatus")
          AnalysisProviderAdministrationOperator.doProvideFunctionalFeatureRunningStatusInfoList(internalOperationDB,analyseResponse)
        case communicationMessage: AnalysisProviderRunningStatusRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "ListAnalysisProviderRunningStatus")
          AnalysisProviderAdministrationOperator.doProvideAnalysisProviderRunningStatusInfoList(internalOperationDB,analyseResponse)
        case communicationMessage: AnalysisProviderRegisterFunctionalFeatureRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "RegisterFunctionalFeature")
          AnalysisProviderAdministrationOperator.doRegisterFunctionalFeature(internalOperationDB,communicationMessage,analyseResponse)
        case communicationMessage: AnalysisProviderUnregisterFunctionalFeatureRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "UnregisterFunctionalFeature")
          AnalysisProviderAdministrationOperator.doUnregisterFunctionalFeature(internalOperationDB,communicationMessage,analyseResponse)

        //for provider analysis service
        case communicationMessage: SpatialPropertiesAggregateStatisticRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "SpatialPropertiesAggregateStatistic")
          SpatialPropertiesStatisticAndAnalysis.doExecuteSpatialPropertiesAggregateStatistic(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest])

        case communicationMessage: AdministrativeDivisionSpatialCalculateRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "AdministrativeDivisionSpatialCalculate")
          AdministrativeDivisionBasedSpatialAnalysis.doExecuteDataSliceAdministrativeDivisionSpatialCalculation(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest])

        case communicationMessage: TemporalDurationBasedSpatialPropertiesStatisticRequest =>
          internalOperationDB.recordFeatureExecution(communicationMessage.getRequestUUID, "TemporalDurationBasedSpatialPropertiesStatistic")
          TemporalDurationBasedSpatialPropertiesStatisticAnalysis.doExecuteTemporalDurationBasedSpatialPropertiesStatistic(
            globalDataAccessor,analyseResponse,communicationMessage.asInstanceOf[TemporalDurationBasedSpatialPropertiesStatisticRequest]
          )
      }
    }

    if(analyseResponse!=null){
      val responseDatetime = new Date
      val requestUUID = analyseResponse.getRequestUUID
      analyseResponse.setResponseDateTime(responseDatetime)
      val instant: Instant = responseDatetime.toInstant
      val localDateTime: LocalDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime
      internalOperationDB.recordFeatureResponse(requestUUID, localDateTime)

      println("Response Analysis: "+requestUUID+ " at: " + localDateTime + "")
      println("----------------------------------------------------------------------------------------")
      senderActor.tell(analyseResponse,communicationActor)
    }
    print("~$ ")
  }
}
