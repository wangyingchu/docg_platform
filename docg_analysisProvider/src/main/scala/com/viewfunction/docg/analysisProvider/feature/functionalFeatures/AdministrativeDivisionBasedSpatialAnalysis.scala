package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.exception.AnalysisProviderRuntimeException
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse, spatialAnalysis}
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleGrade, GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.{AdministrativeDivisionSpatialCalculateRequest, SpatialCommonConfig}
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.DataSliceOperationUtil.{getDataSlicePropertyType, massDataOperationParallelism}
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.{DataSliceOperationConstant, DataSliceOperationUtil, ResponseDataSourceTech}
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util.MassDataOperationUtil
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceOperationResult
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.{DataSlice, DataSlicePropertyType}
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil
import org.apache.spark.api.java.function.ForeachPartitionFunction
import org.apache.spark.sql.{DataFrame, Row}

import java.util
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object AdministrativeDivisionBasedSpatialAnalysis {

  @throws(classOf[AnalysisProviderRuntimeException])
  def doExecuteDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,analyseResponse:AnalyseResponse,
                                                               administrativeDivisionSpatialCalculateRequest:AdministrativeDivisionSpatialCalculateRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset={
    println("------------------------------------------------------------")
    println(" Start execute doExecuteDataSliceAdministrativeDivisionSpatialCalculation ...")
    println("------------------------------------------------------------")
    val dataSlice = administrativeDivisionSpatialCalculateRequest.getSubjectConception
    val sliceGroup = "defaultSliceGroup"
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

    executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor,analyseResponse,dataSlice,sliceGroup,
      calSubjectReturnProperties,calSpatialPredicateType,calGeospatialScaleGrade,
      calAdministrativeDivisionReturnProperties,calGeospatialScaleLevel,sampleValue)
  }

  @throws(classOf[AnalysisProviderRuntimeException])
  def executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,
                                                               analyseResponse:AnalyseResponse,
                                                               dataSlice:String,sliceGroup:String,
                                                               dataSliceAttributes: mutable.Buffer[String],
                                                               spatialPredicateType:SpatialPredicateType,
                                                               geospatialScaleGrade:GeospatialScaleGrade,
                                                               administrativeDivisionAttributes: mutable.Buffer[String],
                                                               geospatialScaleLevel:GeospatialScaleLevel,
                                                               sampleValue:Double):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println("------------------------------------------------------------")
    println(" Start execute executeDataSliceAdministrativeDivisionSpatialCalculation ...")
    println("------------------------------------------------------------")

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

    val newNames = mutable.Buffer[String](dataSlice+"__"+CoreRealmOperationUtil.RealmGlobalUID)
    dataSliceAttributes.foreach(attribute=>{
      newNames += (dataSlice+"__"+attribute)
    })
    newNames += (geospatialScaleGrade+"__"+CoreRealmOperationUtil.RealmGlobalUID)
    administrativeDivisionAttributes.foreach(attribute=>{
      newNames += (geospatialScaleGrade+"__"+attribute)
    })
    val dfRenamed = calculateResultDF.toDF(newNames: _*)
    generateResultDataSet(globalDataAccessor,dfRenamed,analyseResponse)
  }

  private def generateResultDataSet(globalDataAccessor:GlobalDataAccessor,dataFrame:DataFrame,analyseResponse:AnalyseResponse): com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println("------------------------------------------------------------")
    println(" Start execute generateResultDataSet ...")
    println("------------------------------------------------------------")

    val responseDataList = new java.util.ArrayList[java.util.HashMap[String,Object]]
    val structureFields =dataFrame.schema.fields
    val propertiesInfo = new java.util.HashMap[String,String]
    structureFields.foreach(item =>{
      propertiesInfo.put(item.name,item.dataType.typeName)
    })
    val responseDataFormValue = analyseResponse.getResponseDataForm
    val responseDataset = new com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset(propertiesInfo,responseDataList)
    analyseResponse.setResponseData(responseDataset)

    val dataRowArray = dataFrame.collect()
    dataRowArray.foreach(row=>{
      val currentMap = new java.util.HashMap[String,Object]
      responseDataList.add(currentMap)
      structureFields.foreach(fieldStructure=>{
        currentMap.put(fieldStructure.name,row.get(row.fieldIndex(fieldStructure.name)).asInstanceOf[AnyRef])
      })
    })

    if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.STREAM_BACK)){
      //need do nothing
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DATA_SLICE)){
      val dataSliceName:String = analyseResponse.getResponseUUID
      DataSliceOperationUtil.syncDataSliceFromResponseDataset(globalDataAccessor.dataService,dataSliceName,DataSliceOperationConstant.AnalysisResponseDataFormGroup,responseDataset,ResponseDataSourceTech.SPARK)
      responseDataset.clearDataList()
    }

    responseDataset
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
