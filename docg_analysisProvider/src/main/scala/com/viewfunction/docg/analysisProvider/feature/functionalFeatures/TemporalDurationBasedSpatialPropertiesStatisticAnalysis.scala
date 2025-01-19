package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.{AnalysisResponseCode, GlobalDataAccessor, ResultDataSetUtil}
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
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions.{avg, count, lit, max, min, stddev, sum, variance}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.temporal.ChronoUnit
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object TemporalDurationBasedSpatialPropertiesStatisticAnalysis {

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

    val statisticResultTemporalProperty =
    if (statisticRequest.getStatisticResultTemporalProperty != null) statisticRequest.getStatisticResultTemporalProperty else objectTemporalProperty

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
    //objectConceptionSpDF.printSchema()

    val loopPartObjectConceptionSpDFName = objectConceptionSpDFName+"calculateLoop"
    val objectConception_spatialQueryParam = spatial.SpatialQueryParam(loopPartObjectConceptionSpDFName,objectConceptionSpatialAttributeName,mutable.Buffer[String](objectStatisticProperty,objectTemporalProperty))

    val resultDataSetUtil = new ResultDataSetUtil
    var dfRenamed:DataFrame = null
    var dfRenamedWithTimeWindow:DataFrame = null
    var resultUnionDFWithTimeWindow:DataFrame = null

    val startLocalDateTimeValue: Long = statisticRequest.getStatisticStartTime
    val startLocalDateTimeInstant = Instant.ofEpochMilli(startLocalDateTimeValue)
    var startLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(startLocalDateTimeInstant, ZoneId.systemDefault())
    val endLocalDateTimeValue: Long = statisticRequest.getStatisticEndTime
    val endLocalDateTimeInstant = Instant.ofEpochMilli(endLocalDateTimeValue)
    val endLocalDateTime: LocalDateTime = LocalDateTime.ofInstant(endLocalDateTimeInstant, ZoneId.systemDefault())

    val temporalDurationType: ChronoUnit = statisticRequest.getTemporalDurationType
    val durationCount: Long = statisticRequest.getDurationCount
    var stepLocalDateTime:LocalDateTime = startLocalDateTime.plus(durationCount,temporalDurationType)

    var currentLoopTimeWindowStartDateTime:Long = 0
    var currentLoopDataFilterSparkSQL:String = "SELECT * FROM "+objectConceptionSpDFName

    while (getLongLocalDateTimeValue(stepLocalDateTime) <= getLongLocalDateTimeValue(endLocalDateTime)) {
      currentLoopTimeWindowStartDateTime = getLongLocalDateTimeValue(stepLocalDateTime)
      currentLoopDataFilterSparkSQL = "SELECT * FROM "+objectConceptionSpDFName+" WHERE "+ objectTemporalProperty+" BETWEEN "+
        getLongLocalDateTimeValue(startLocalDateTime) + " AND "+getLongLocalDateTimeValue(stepLocalDateTime)

      globalDataAccessor._getDataFrameFromSparkSQL(loopPartObjectConceptionSpDFName,currentLoopDataFilterSparkSQL)
      val subject_objectSpJoinDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,subjectConception_spatialQueryParam,spatialPredicateType,objectConception_spatialQueryParam,subject_objectSpJoinDFName)
      //subject_objectSpJoinDF.printSchema()

      //统计主体空间相关的客体的计算属性的聚合值
      var subject_objectAggResultDF:DataFrame = null
      var aggregateColumnName:String = ""
      objectAggregationType match {
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.SUM =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(sum(objectStatisticProperty))
          aggregateColumnName = "sum("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.AVG =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(avg(objectStatisticProperty))
          aggregateColumnName = "avg("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.STDDEV =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(stddev(objectStatisticProperty))
          aggregateColumnName = "stddev_samp("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.COUNT =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(count(objectStatisticProperty))
          aggregateColumnName = "count("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.MAX =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(max(objectStatisticProperty))
          aggregateColumnName = "max("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.MIN =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(min(objectStatisticProperty))
          aggregateColumnName = "min("+objectStatisticProperty+")"
        case spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.VARIANCE =>
          subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(variance(objectStatisticProperty))
          aggregateColumnName = "var_samp("+objectStatisticProperty+")"
      }
      //subject_objectAggResultDF.printSchema()

      //join 初始主体 df，获取相关属性信息
      val mergedSubjectStaticResultDF = subject_objectAggResultDF.join(subjectConceptionSpDF,subjectIdentityProperty)
      //mergedSubjectStaticResultDF.printSchema()

      //过滤所需的属性信息
      val propertiesList:ArrayBuffer[String] = ArrayBuffer[String](aggregateColumnName)

      if(subjectReturnProperties!=null){
        subjectReturnProperties.foreach(propItem=>{
          if(!propItem.equalsIgnoreCase(CoreRealmOperationUtil.RealmGlobalUID)){
            propertiesList += propItem
          }
        })
      }

      val filterResDF = mergedSubjectStaticResultDF.select(subjectIdentityProperty,propertiesList:_*)
      //filterResDF.printSchema()

      val newNames = mutable.Buffer[String](CoreRealmOperationUtil.RealmGlobalUID)
      propertiesList.foreach(attribute=>{
        var tempStr = attribute.replaceAll("\\(","__")
        tempStr = tempStr.replaceAll("\\)","")
        newNames += tempStr
      })

      dfRenamed = filterResDF.toDF(newNames: _*)
      //dfRenamed.printSchema()
      dfRenamedWithTimeWindow = dfRenamed.withColumn(statisticResultTemporalProperty, lit(currentLoopTimeWindowStartDateTime))

      if(resultUnionDFWithTimeWindow == null){
        resultUnionDFWithTimeWindow = dfRenamedWithTimeWindow
      }else{
        resultUnionDFWithTimeWindow = resultUnionDFWithTimeWindow.union(dfRenamedWithTimeWindow)
      }

      startLocalDateTime = getLocalDateTime(stepLocalDateTime.getYear,
        stepLocalDateTime.getMonthValue,
        stepLocalDateTime.getDayOfMonth,
        stepLocalDateTime.getHour,
        stepLocalDateTime.getMinute,
        stepLocalDateTime.getSecond)

      stepLocalDateTime = stepLocalDateTime.plus(durationCount,temporalDurationType)
    }

    analyseResponse.setResponseCode(AnalysisResponseCode.ANALYSUS_SUCCESS.toString)
    analyseResponse.setResponseSummary("AnalysisResponse of SpatialPropertiesStatisticAndAnalysis")
    resultDataSetUtil.generateResultDataSet(globalDataAccessor,resultUnionDFWithTimeWindow,analyseResponse,statisticRequest)
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

  private def getLongLocalDateTimeValue(localDateTime: LocalDateTime): Long = {
    val zdt = localDateTime.atZone(ZoneId.systemDefault())
    val instant = zdt.toInstant()
    val milliseconds = instant.toEpochMilli()
    milliseconds
  }

  private def getLocalDateTime(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int): LocalDateTime = {
    LocalDateTime.of(year, month, dayOfMonth, hour, minute, second)
  }
}
