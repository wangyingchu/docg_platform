package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.exception.AnalysisProviderRuntimeException
import com.viewfunction.docg.analysisProvider.feature.common.{AnalysisResponseCode, GlobalDataAccessor, ResultDataSetUtil}
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse, spatialAnalysis}
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleGrade, GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialCommonConfig}
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.DataSliceOperationConstant
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object AdministrativeDivisionBasedSpatialAnalysis {

  @throws(classOf[AnalysisProviderRuntimeException])
  def doExecuteDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,
                                                                 analyseResponse:AnalyseResponse,
                                                                 administrativeDivisionSpatialCalculateRequest:AdministrativeDivisionSpatialCalculateRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset={
    println(" Start execute doExecuteDataSliceAdministrativeDivisionSpatialCalculation ...")

    val dataSlice = administrativeDivisionSpatialCalculateRequest.getSubjectConception
    var sliceGroup = DataSliceOperationConstant.DefaultDataSliceGroup
    if(administrativeDivisionSpatialCalculateRequest.getSubjectGroup!= null){
      sliceGroup = administrativeDivisionSpatialCalculateRequest.getSubjectGroup
    }

    val sampleValue:Double = administrativeDivisionSpatialCalculateRequest.getSampleValue

    val subjectReturnProperties:Array[String] = administrativeDivisionSpatialCalculateRequest.getSubjectReturnProperties
    val calSubjectReturnProperties:ArrayBuffer[String] = ArrayBuffer[String]()
    if(subjectReturnProperties!=null){
      subjectReturnProperties.foreach(propItem=>{
        calSubjectReturnProperties += propItem
      })
    }

    val administrativeDivisionReturnProperties:Array[String] = administrativeDivisionSpatialCalculateRequest.getAdministrativeDivisionReturnProperties
    val calAdministrativeDivisionReturnProperties:ArrayBuffer[String] = ArrayBuffer[String]()
    if(administrativeDivisionReturnProperties!=null){
      administrativeDivisionReturnProperties.foreach(propItem=>{
        calAdministrativeDivisionReturnProperties += propItem
      })
    }

    var calSpatialPredicateType:SpatialPredicateType = SpatialPredicateType.Within
    val predicateType:SpatialCommonConfig.PredicateType = administrativeDivisionSpatialCalculateRequest.getPredicateType
    predicateType match {
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Contains =>
        calSpatialPredicateType = SpatialPredicateType.Contains
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Intersects =>
        calSpatialPredicateType = SpatialPredicateType.Intersects
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Crosses =>
        calSpatialPredicateType = SpatialPredicateType.Crosses
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Within =>
        calSpatialPredicateType = SpatialPredicateType.Within
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Equals =>
        calSpatialPredicateType = SpatialPredicateType.Equals
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Touches =>
        calSpatialPredicateType = SpatialPredicateType.Touches
      case spatialAnalysis.SpatialCommonConfig.PredicateType.Overlaps =>
        calSpatialPredicateType = SpatialPredicateType.Overlaps
    }

    var calGeospatialScaleGrade:GeospatialScaleGrade = GeospatialScaleGrade.PREFECTURE
    val geospatialScaleGrade:SpatialCommonConfig.GeospatialScaleGrade = administrativeDivisionSpatialCalculateRequest.getGeospatialScaleGrade
    geospatialScaleGrade match {
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Continent =>
        calGeospatialScaleGrade= GeospatialScaleGrade.CONTINENT
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Country_Region =>
        calGeospatialScaleGrade= GeospatialScaleGrade.COUNTRY_REGION
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Province =>
        calGeospatialScaleGrade= GeospatialScaleGrade.PROVINCE
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Prefecture =>
        calGeospatialScaleGrade= GeospatialScaleGrade.PREFECTURE
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.County =>
        calGeospatialScaleGrade= GeospatialScaleGrade.COUNTY
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Township =>
        calGeospatialScaleGrade= GeospatialScaleGrade.TOWNSHIP
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleGrade.Village =>
        calGeospatialScaleGrade= GeospatialScaleGrade.VILLAGE
    }

    var calGeospatialScaleLevel:GeospatialScaleLevel = GeospatialScaleLevel.CountryLevel
    val geospatialScaleLevel:SpatialCommonConfig.GeospatialScaleLevel = administrativeDivisionSpatialCalculateRequest.getGeospatialScaleLevel
    geospatialScaleLevel match{
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel =>
        calGeospatialScaleLevel = GeospatialScaleLevel.GlobalLevel
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleLevel.CountryLevel =>
        calGeospatialScaleLevel = GeospatialScaleLevel.CountryLevel
      case spatialAnalysis.SpatialCommonConfig.GeospatialScaleLevel.LocalLevel =>
        calGeospatialScaleLevel = GeospatialScaleLevel.LocalLevel
    }

    executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor,analyseResponse,administrativeDivisionSpatialCalculateRequest,
      dataSlice,sliceGroup,calSubjectReturnProperties,calSpatialPredicateType,calGeospatialScaleGrade,
      calAdministrativeDivisionReturnProperties,calGeospatialScaleLevel,sampleValue)
  }

  @throws(classOf[AnalysisProviderRuntimeException])
  def executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,
                                                               analyseResponse:AnalyseResponse,
                                                               administrativeDivisionSpatialCalculateRequest:AdministrativeDivisionSpatialCalculateRequest,
                                                               dataSlice:String,sliceGroup:String,
                                                               dataSliceAttributes: mutable.Buffer[String],
                                                               spatialPredicateType:SpatialPredicateType,
                                                               geospatialScaleGrade:GeospatialScaleGrade,
                                                               administrativeDivisionAttributes: mutable.Buffer[String],
                                                               geospatialScaleLevel:GeospatialScaleLevel,
                                                               sampleValue:Double):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println(" Start execute executeDataSliceAdministrativeDivisionSpatialCalculation ...")

    if(sampleValue<=0 | sampleValue>1){
      throw new AnalysisProviderRuntimeException("sampleValue should in (0,1] range")
    }

    val dataSliceGeometryContent = getGeospatialGeometryContent(geospatialScaleLevel)
    var dataSliceSpatialDFName = dataSlice+"_SPDF"
    val dataSliceSpatialAttributeName = dataSlice+"_SPAttr"
    val dataSliceSpatialDF =
      globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(dataSlice,sliceGroup,dataSliceGeometryContent,dataSliceSpatialDFName,dataSliceSpatialAttributeName)

    if(sampleValue != 1.0){
      val sampledDataSliceSpatialDF = dataSliceSpatialDF.sample(sampleValue)
      dataSliceSpatialDFName = dataSlice+"_SPDF_Sample"
      sampledDataSliceSpatialDF.createOrReplaceTempView(dataSliceSpatialDFName)
    }

    val administrativeDivisionDataSlice = getAdministrativeDivisionDataSliceName(geospatialScaleGrade)
    val administrativeDivisionSpatialDFName = administrativeDivisionDataSlice+"_SPDF"
    val administrativeDivisionSpatialAttributeName = administrativeDivisionDataSlice+"_SPAttr"

    globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(administrativeDivisionDataSlice,
        SpatialAnalysisConstant.GeospatialScaleDataSliceSystemGroup, dataSliceGeometryContent,administrativeDivisionSpatialDFName,administrativeDivisionSpatialAttributeName)

    val dataSliceAttributesBuffer = mutable.Buffer[String](CoreRealmOperationUtil.RealmGlobalUID)
    dataSliceAttributes.foreach(attribute=>{
      dataSliceAttributesBuffer += attribute
    })
    val dataSlice_spatialQueryParam = spatial.SpatialQueryParam(dataSliceSpatialDFName,dataSliceSpatialAttributeName,dataSliceAttributesBuffer)

    val administrativeDivisionAttributesBuffer = mutable.Buffer[String](CoreRealmOperationUtil.RealmGlobalUID)
    administrativeDivisionAttributes.foreach(attribute=>{
      administrativeDivisionAttributesBuffer += attribute
    })
    val administrativeDivision_spatialQueryParam = spatial.SpatialQueryParam(administrativeDivisionSpatialDFName,administrativeDivisionSpatialAttributeName,administrativeDivisionAttributesBuffer)

    val spatialQueryMetaFunction = new SpatialQueryMetaFunction
    val calculateResultDFName = "calculateResultJoinDF"
    val calculateResultDF =
      spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,dataSlice_spatialQueryParam,spatialPredicateType,administrativeDivision_spatialQueryParam,calculateResultDFName)

    val newNames: scala.collection.mutable.Buffer[String] = mutable.Buffer[String](dataSlice+"__"+CoreRealmOperationUtil.RealmGlobalUID)
    dataSliceAttributes.foreach(attribute=>{
      newNames += (dataSlice+"__"+attribute)
    })
    newNames += (geospatialScaleGrade+"__"+CoreRealmOperationUtil.RealmGlobalUID)
    administrativeDivisionAttributes.foreach(attribute=>{
      newNames += (geospatialScaleGrade+"__"+attribute)
    })
    val dfRenamed = calculateResultDF.toDF(newNames.toSeq: _*)

    analyseResponse.setResponseCode(AnalysisResponseCode.ANALYSUS_SUCCESS.toString)
    analyseResponse.setResponseSummary("AnalysisResponse of AdministrativeDivisionBasedSpatialAnalysis")

    val resultDataSetUtil = new ResultDataSetUtil
    resultDataSetUtil.generateResultDataSet(globalDataAccessor,dfRenamed,analyseResponse,administrativeDivisionSpatialCalculateRequest)
  }

  private def getGeospatialGeometryContent(geospatialScaleLevel:GeospatialScaleLevel):String = {
    var runtimeGeometryContent:String = null
    geospatialScaleLevel match {
      case GeospatialScaleLevel.GlobalLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialGLGeometryContent
      case GeospatialScaleLevel.CountryLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialCLGeometryContent
      case GeospatialScaleLevel.LocalLevel =>
        runtimeGeometryContent = RealmConstant._GeospatialLLGeometryContent
    }
    runtimeGeometryContent
  }

  private def getAdministrativeDivisionDataSliceName(geospatialScaleGrade:GeospatialScaleGrade):String = {
    var runtimeAdministrativeDivisionDataSliceName:String = null
    geospatialScaleGrade match {
      case GeospatialScaleGrade.CONTINENT =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleContinentDataSlice
      case GeospatialScaleGrade.COUNTRY_REGION =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleCountryRegionDataSlice
      case GeospatialScaleGrade.PROVINCE =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleProvinceDataSlice
      case GeospatialScaleGrade.PREFECTURE =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScalePrefectureDataSlice
      case GeospatialScaleGrade.COUNTY =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleCountyDataSlice
      case GeospatialScaleGrade.TOWNSHIP =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleTownshipDataSlice
      case GeospatialScaleGrade.VILLAGE =>
        runtimeAdministrativeDivisionDataSliceName = SpatialAnalysisConstant.GeospatialScaleVillageDataSlice
    }
    runtimeAdministrativeDivisionDataSliceName
  }
}
