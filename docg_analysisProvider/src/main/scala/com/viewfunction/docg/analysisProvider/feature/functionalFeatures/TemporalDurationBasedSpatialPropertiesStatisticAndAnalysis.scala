package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.{GlobalDataAccessor, ResultDataSetUtil}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseResponse, spatialAnalysis}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig.PredicateType
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{SpatialCommonConfig, SpatialPropertiesAggregateStatisticRequest, TemporalDurationBasedSpatialPropertiesStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.SpatialPropertiesStatisticAndAnalysis.{getGeospatialGeometryContent, sliceGroupName}
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant

import scala.collection.mutable

object TemporalDurationBasedSpatialPropertiesStatisticAndAnalysis {

  def doExecuteTemporalDurationBasedSpatialPropertiesStatistic(globalDataAccessor:GlobalDataAccessor,
                                                   analyseResponse:AnalyseResponse,
                                                   statisticRequest:TemporalDurationBasedSpatialPropertiesStatisticRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println(" Start execute doExecuteTemporalDurationBasedSpatialPropertiesStatistic ...")
    val subjectConception = statisticRequest.getSubjectConception
    val objectConception = statisticRequest.getObjectConception
    val predicateType:PredicateType = statisticRequest.getPredicateType

    val subjectIdentityProperty = statisticRequest.getSubjectIdentityProperty
    val subjectReturnProperties:Array[String] = statisticRequest.getSubjectReturnProperties

    val objectAggregationType:ObjectAggregationType = statisticRequest.getObjectAggregationType
    val objectStatisticProperty = statisticRequest.getObjectStatisticProperty
    val objectTemporalProperty = statisticRequest.getObjectTemporalProperty

    val spatialQueryMetaFunction = new SpatialQueryMetaFunction

    //获取Subject conception(主体) 空间dataframe
    val subjectConceptionSpDFName = "subjectConceptionSpDF"
    val subjectConceptionSpatialAttributeName = "subjectConceptionGeoAttr"
    if(statisticRequest.getSubjectGroup != null){
      sliceGroupName = statisticRequest.getSubjectGroup
    }
    var spatialValueProperty = getGeospatialGeometryContent(SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel)
    if(statisticRequest.getGeospatialScaleLevel != null) {
      spatialValueProperty = getGeospatialGeometryContent(statisticRequest.getGeospatialScaleLevel)
    }

    val subjectConceptionSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(subjectConception,sliceGroupName,spatialValueProperty,subjectConceptionSpDFName,subjectConceptionSpatialAttributeName)
    //subjectConceptionSpDF.printSchema()
    val subjectConception_spatialQueryParam = spatial.SpatialQueryParam(subjectConceptionSpDFName,subjectConceptionSpatialAttributeName,mutable.Buffer[String](subjectIdentityProperty))

    //设定主体客体见的空间join计算逻辑
    val subject_objectSpJoinDFName = "subject_objectSpJoinDF"
    var spatialPredicateType:SpatialPredicateType = SpatialPredicateType.Contains
    predicateType match {
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Contains =>
        spatialPredicateType = SpatialPredicateType.Contains
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Intersects =>
        spatialPredicateType = SpatialPredicateType.Intersects
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Crosses =>
        spatialPredicateType = SpatialPredicateType.Crosses
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Within =>
        spatialPredicateType = SpatialPredicateType.Within
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Equals =>
        spatialPredicateType = SpatialPredicateType.Equals
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Touches =>
        spatialPredicateType = SpatialPredicateType.Touches
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Overlaps =>
        spatialPredicateType = SpatialPredicateType.Overlaps
    }

    //获取Object conception(客体) 空间dataframe
    val objectConceptionSpDFName = "objectConceptionSpDF"
    val objectConceptionSpatialAttributeName = "objectConceptionGeoAttr"
    if(statisticRequest.getObjectGroup != null){
      sliceGroupName = statisticRequest.getObjectGroup
    }
    val objectConceptionSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(objectConception,sliceGroupName,spatialValueProperty,objectConceptionSpDFName,objectConceptionSpatialAttributeName)
    val loopPartObjectConceptionSpDFName = objectConceptionSpDFName+"calculateLoop"
    val objectConception_spatialQueryParam = spatial.SpatialQueryParam(loopPartObjectConceptionSpDFName,objectConceptionSpatialAttributeName,mutable.Buffer[String](objectStatisticProperty,objectTemporalProperty))



    //in loop logic
    var currentLoopDataFilterSparkSQL:String = "SELECT * FROM "+objectConceptionSpDFName+" WHERE "+ objectTemporalProperty+" = '1'"
    globalDataAccessor._getDataFrameFromSparkSQL(loopPartObjectConceptionSpDFName,currentLoopDataFilterSparkSQL)
    val subject_objectSpJoinDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,subjectConception_spatialQueryParam,spatialPredicateType,objectConception_spatialQueryParam,subject_objectSpJoinDFName)








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
