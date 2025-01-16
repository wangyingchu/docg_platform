package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.{GlobalDataAccessor, ResultDataSetUtil}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{SpatialCommonConfig, SpatialPropertiesAggregateStatisticRequest, TemporalDurationBasedSpatialPropertiesStatisticRequest}
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant

object TemporalDurationBasedSpatialPropertiesStatisticAndAnalysis {

  def doExecuteTemporalDurationBasedSpatialPropertiesStatistic(globalDataAccessor:GlobalDataAccessor,
                                                   analyseResponse:AnalyseResponse,
                                                   statisticRequest:TemporalDurationBasedSpatialPropertiesStatisticRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    val resultDataSetUtil = new ResultDataSetUtil
    resultDataSetUtil.generateResultDataSet(globalDataAccessor,null,analyseResponse,statisticRequest)
  }

  private def getGeospatialGeometryContent(geospatialScaleLevel:SpatialCommonConfig.GeospatialScaleLevel):String = {
    var runtimeGeometryContent:String = null
    geospatialScaleLevel match {
      case SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialGLGeometryContent
      case SpatialCommonConfig.GeospatialScaleLevel.CountryLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialCLGeometryContent
      case SpatialCommonConfig.GeospatialScaleLevel.LocalLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialLLGeometryContent
    }
    runtimeGeometryContent
  }
}
