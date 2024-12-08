package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.{GlobalDataAccessor, ResultDataSetUtil}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseResponse, spatialAnalysis}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig.PredicateType
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.{CalculationOperator, ObjectAggregationType}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{SpatialCommonConfig, SpatialPropertiesAggregateStatisticRequest}
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.DataSliceOperationConstant
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.functions.{avg, count, max, min, stddev, sum, variance}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object SpatialPropertiesStatisticAndAnalysis {

  var sliceGroupName = DataSliceOperationConstant.DefaultDataSliceGroup

  def doExecuteSpatialPropertiesAggregateStatistic(globalDataAccessor:GlobalDataAccessor,
                                                 analyseResponse:AnalyseResponse,
                                                 statisticRequest:SpatialPropertiesAggregateStatisticRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println(" Start execute doExecuteSpatialPropertiesAggregateStatistic ...")
    val objectConception = statisticRequest.getObjectConception
    val subjectConception = statisticRequest.getSubjectConception
    val predicateType:PredicateType = statisticRequest.getPredicateType

    val subjectIdentityProperty = statisticRequest.getSubjectIdentityProperty
    val subjectCalculationProperty = statisticRequest.getSubjectCalculationProperty

    val objectCalculationProperty = statisticRequest.getObjectCalculationProperty
    val objectAggregationType:ObjectAggregationType = statisticRequest.getObjectAggregationType

    val subjectReturnProperties:Array[String] = statisticRequest.getSubjectReturnProperties
    val statisticResultProperty = statisticRequest.getStatisticResultProperty
    val calculationOperator:CalculationOperator = statisticRequest.getCalculationOperator

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

    //获取Object conception(客体) 空间dataframe
    val objectConceptionSpDFName = "objectConceptionSpDF"
    val objectConceptionSpatialAttributeName = "objectConceptionGeoAttr"
    if(statisticRequest.getObjectGroup != null){
      sliceGroupName = statisticRequest.getObjectGroup
    }
    val objectConceptionSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(objectConception,sliceGroupName,spatialValueProperty,objectConceptionSpDFName,objectConceptionSpatialAttributeName)
    //objectConceptionSpDF.printSchema()
    val objectConception_spatialQueryParam = spatial.SpatialQueryParam(objectConceptionSpDFName,objectConceptionSpatialAttributeName,mutable.Buffer[String](objectCalculationProperty))

    //执行主体客体见的空间join计算
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

    val subject_objectSpJoinDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,subjectConception_spatialQueryParam,spatialPredicateType,objectConception_spatialQueryParam,subject_objectSpJoinDFName)
    //subject_objectSpJoinDF.printSchema()

    //统计主体空间相关的客体的计算属性的聚合值
    var subject_objectAggResultDF:DataFrame = null
    var aggregateColumnName:String = ""
    objectAggregationType match {
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.SUM =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(sum(objectCalculationProperty))
        aggregateColumnName = "sum("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.AVG =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(avg(objectCalculationProperty))
        aggregateColumnName = "avg("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.STDDEV =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(stddev(objectCalculationProperty))
        aggregateColumnName = "stddev_samp("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.COUNT =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(count(objectCalculationProperty))
        aggregateColumnName = "count("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.MAX =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(max(objectCalculationProperty))
        aggregateColumnName = "max("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.MIN =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(min(objectCalculationProperty))
        aggregateColumnName = "min("+objectCalculationProperty+")"
      case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.VARIANCE =>
        subject_objectAggResultDF = subject_objectSpJoinDF.groupBy(subjectIdentityProperty).agg(variance(objectCalculationProperty))
        aggregateColumnName = "var_samp("+objectCalculationProperty+")"
    }
    //subject_objectAggResultDF.printSchema()

    //join 初始主体 df，获取相关属性信息
    val mergedSubjectStaticResultDF = subject_objectAggResultDF.join(subjectConceptionSpDF,subjectIdentityProperty)
    //mergedSubjectStaticResultDF.printSchema()

    //过滤所需的属性信息
    val propertiesList:ArrayBuffer[String] = ArrayBuffer[String](aggregateColumnName)
    if(subjectCalculationProperty != null){
      propertiesList += subjectCalculationProperty
    }
    if(subjectReturnProperties!=null){
      subjectReturnProperties.foreach(propItem=>{
        propertiesList += propItem
      })
    }
    val filterResDF = mergedSubjectStaticResultDF.select(subjectIdentityProperty,propertiesList:_*)
    //filterResDF.printSchema()

    val newNames = mutable.Buffer[String](CoreRealmOperationUtil.RealmGlobalUID)
    propertiesList.foreach(attribute=>{
      println(attribute)
      var tempStr = attribute.replaceAll("\\(","__")
      tempStr = tempStr.replaceAll("\\)","")
        newNames += tempStr
      })

    val resultDataSetUtil = new ResultDataSetUtil

    //执行主体与客体的聚合统计值的数值计算
    if(subjectCalculationProperty != null && calculationOperator!= null && statisticResultProperty != null){
      val calculationDF = filterResDF.select(subjectIdentityProperty,aggregateColumnName,subjectCalculationProperty)
      val calculationResultRDD = calculationDF.rdd.map(row=>{
        var calValue = 0.0
        calculationOperator match{
          case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Add =>
            calValue = row.get(1).asInstanceOf[Double] + row.get(2).asInstanceOf[Double]
          case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Subtract =>
            calValue = row.get(1).asInstanceOf[Double] - row.get(2).asInstanceOf[Double]
          case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Multiply =>
            calValue = row.get(1).asInstanceOf[Double] * row.get(2).asInstanceOf[Double]
          case spatialAnalysis.SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Divide =>
            calValue = row.get(1).asInstanceOf[Double] / row.get(2).asInstanceOf[Double]
        }
        Row(row.get(0),calValue)
      })
      val schema = StructType(
        Seq(
          calculationDF.schema.fields(0),//fields(0) should be subjectIdentityProperty
          StructField(statisticResultProperty,DoubleType,true)
        )
      )
      val calculationResultDF = globalDataAccessor.getSparkSession().createDataFrame(calculationResultRDD,schema)
      //calculationResultDF.printSchema()

      val finalCalculatedDF = filterResDF.join(calculationResultDF,subjectIdentityProperty)
      //finalCalculatedDF.printSchema()

      val dfRenamed = finalCalculatedDF.toDF(newNames: _*)
      //dfRenamed.printSchema()
      resultDataSetUtil.generateResultDataSet(globalDataAccessor,dfRenamed,analyseResponse)
    }else{
      val dfRenamed = filterResDF.toDF(newNames: _*)
      //dfRenamed.printSchema()
      resultDataSetUtil.generateResultDataSet(globalDataAccessor,dfRenamed,analyseResponse)
    }
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
