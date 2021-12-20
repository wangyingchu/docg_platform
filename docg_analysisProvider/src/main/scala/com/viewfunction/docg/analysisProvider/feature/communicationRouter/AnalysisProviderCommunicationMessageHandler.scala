package com.viewfunction.docg.analysisProvider.feature.communicationRouter

import akka.actor.ActorRef
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationMessageHandler
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialPropertiesAggregateStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.{AdministrativeDivisionBasedSpatialAnalysis, EcologicalEnvironmentAnalysis, SpatialPropertiesStatisticAndAnalysis}

class AnalysisProviderCommunicationMessageHandler(globalDataAccessor :GlobalDataAccessor) extends CommunicationMessageHandler{
  override def handleMessage(communicationMessage: Any, communicationActor: ActorRef, senderActor: ActorRef): Unit = {

    communicationMessage match {
      case communicationMessage: String =>
        println(s" $communicationMessage")
      case communicationMessage: com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection =>
        //senderActor.tell("Reply for AnalyzeTreesCrownAreaInSection Executed "+communicationMessage.getRequestUUID , communicationActor)
        println(communicationMessage.getTreeCrownType+" "+communicationMessage.getRequestUUID+" "+communicationMessage.getRequestDateTime)
        val result = EcologicalEnvironmentAnalysis.executeSparkTreesCrownAreaCal2(globalDataAccessor,"TreeCanopy","CommunityReportingArea")
        val analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.generateMetaInfo()
        analyseResponse.setResponseData(result)
        senderActor.tell(analyseResponse,communicationActor)

      case communicationMessage: SpatialPropertiesAggregateStatisticRequest =>
        val result = SpatialPropertiesStatisticAndAnalysis.executeSpatialPropertiesAggregateStatistic(globalDataAccessor,communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest])
        val analyseResponse = new AnalyseResponse(communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest].getRequestUUID)
        analyseResponse.generateMetaInfo()
        analyseResponse.setResponseData(result)
        senderActor.tell(analyseResponse,communicationActor)

      case   communicationMessage: AdministrativeDivisionSpatialCalculateRequest =>
        val result = AdministrativeDivisionBasedSpatialAnalysis.doExecuteDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor,communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest])
        val analyseResponse = new AnalyseResponse(communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest].getRequestUUID)
        analyseResponse.generateMetaInfo()
        analyseResponse.setResponseData(result)
        senderActor.tell(analyseResponse,communicationActor)
    }
  }
}
