package com.viewfunction.docg.analysisProvider.feature.communicationRouter

import akka.actor.ActorRef
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse, ResponseDataset}
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationMessageHandler
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialPropertiesAggregateStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.{AdministrativeDivisionBasedSpatialAnalysis, EcologicalEnvironmentAnalysis, SpatialPropertiesStatisticAndAnalysis}

class AnalysisProviderCommunicationMessageHandler(globalDataAccessor :GlobalDataAccessor) extends CommunicationMessageHandler{
  override def handleMessage(communicationMessage: Any, communicationActor: ActorRef, senderActor: ActorRef): Unit = {

    var analyseResponse:AnalyseResponse=null

    communicationMessage match {
      case communicationMessage:AnalyseRequest =>
        analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.setResponseDataForm(communicationMessage.getResponseDataForm)
    }

    communicationMessage match {
      case communicationMessage: String =>
        println(s" $communicationMessage")
      /*
      case communicationMessage: com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection =>
        //senderActor.tell("Reply for AnalyzeTreesCrownAreaInSection Executed "+communicationMessage.getRequestUUID , communicationActor)
        println(communicationMessage.getTreeCrownType+" "+communicationMessage.getRequestUUID+" "+communicationMessage.getRequestDateTime)
        val result = EcologicalEnvironmentAnalysis.executeSparkTreesCrownAreaCal2(globalDataAccessor,"TreeCanopy","CommunityReportingArea")
        val analyseResponse = new AnalyseResponse(communicationMessage.getRequestUUID)
        analyseResponse.generateMetaInfo()
        analyseResponse.setResponseData(result)
        analyseResponse.setResponseDataForm(communicationMessage.getResponseDataForm)
        senderActor.tell(analyseResponse,communicationActor)
      */
      case communicationMessage: SpatialPropertiesAggregateStatisticRequest =>
        if(analyseResponse!=null){
          val result = SpatialPropertiesStatisticAndAnalysis.executeSpatialPropertiesAggregateStatistic(globalDataAccessor,communicationMessage.asInstanceOf[SpatialPropertiesAggregateStatisticRequest])
          setupResponseDataAccordingToRequestForm(analyseResponse,result)
        }

      case communicationMessage: AdministrativeDivisionSpatialCalculateRequest =>
        if(analyseResponse!=null){
          val result = AdministrativeDivisionBasedSpatialAnalysis.doExecuteDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor,communicationMessage.asInstanceOf[AdministrativeDivisionSpatialCalculateRequest])
          setupResponseDataAccordingToRequestForm(analyseResponse,result)
        }
    }
    if(analyseResponse!=null){
      analyseResponse.generateMetaInfo()
      senderActor.tell(analyseResponse,communicationActor)
    }
  }

  def setupResponseDataAccordingToRequestForm(analyseResponse:AnalyseResponse,responseDataset:ResponseDataset):Unit = {
      val responseDataFormValue = analyseResponse.getResponseDataForm
      if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.STREAM_BACK)){
      }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DATA_SLICE)){
        // add datalist to data slice responseDataset.getDataList ....
        //clear datalist content
        responseDataset.clearDataList()
        //analyseResponse.setResponseData(responseDataset)
      }
      analyseResponse.setResponseData(responseDataset)
  }
}
