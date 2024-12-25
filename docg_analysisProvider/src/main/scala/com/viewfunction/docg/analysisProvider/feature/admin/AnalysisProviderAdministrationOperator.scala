package com.viewfunction.docg.analysisProvider.feature.admin

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse
import com.viewfunction.docg.analysisProvider.providerApplication.util.InternalOperationDB
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.{FeatureRunningInfo, FunctionalFeatureInfo, ProviderRunningInfo}

import java.util

object AnalysisProviderAdministrationOperator {

  def doProvideFunctionalFeaturesInfoList(internalOperationDB:InternalOperationDB,analyseResponse:AnalyseResponse):Unit = {
    val resultList : util.ArrayList[FunctionalFeatureInfo] = internalOperationDB.listFunctionalFeaturesInfo()
    analyseResponse.setResponseData(resultList)
  }

  def doProvideFunctionalFeatureRunningStatusInfoList(internalOperationDB:InternalOperationDB,analyseResponse:AnalyseResponse):Unit = {
    val resultList : util.ArrayList[FeatureRunningInfo] = internalOperationDB.listFunctionalFeatureRunningStatus()
    analyseResponse.setResponseData(resultList)
  }

  def doProvideAnalysisProviderRunningStatusInfoList(internalOperationDB:InternalOperationDB,analyseResponse:AnalyseResponse):Unit = {
    val resultList : util.ArrayList[ProviderRunningInfo] = internalOperationDB.listAnalysisProviderRunningStatus()
    analyseResponse.setResponseData(resultList)
  }
}
