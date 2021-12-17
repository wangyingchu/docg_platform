package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleGrade, GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType

import scala.collection.mutable

object AdministrativeDivisionBasedSpatialAnalysis {

  def executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,
                                                               dataSlice:String,sliceGroup:String,
                                                               dataSliceAttributes: mutable.Buffer[String],
                                                               spatialPredicateType:SpatialPredicateType,
                                                               geospatialScaleGrade:GeospatialScaleGrade,
                                                               administrativeDivisionAttributes: mutable.Buffer[String],
                                                               geospatialScaleLevel:GeospatialScaleLevel):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    val dataSliceGeometryContent = getGeospatialGeometryContent(geospatialScaleLevel)
    val dataSliceSpatialDFName = dataSlice+"_SPDF"
    val dataSliceSpatialAttributeName = dataSlice+"_SPAttr"
    //val dataSliceSpatialDF =
      globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(dataSlice,sliceGroup,dataSliceGeometryContent,dataSliceSpatialDFName,dataSliceSpatialAttributeName)

    val administrativeDivisionDataSlice = getAdministrativeDivisionDataSliceName(geospatialScaleGrade)
    val administrativeDivisionSpatialDFName = administrativeDivisionDataSlice+"_SPDF"
    val administrativeDivisionSpatialAttributeName = administrativeDivisionDataSlice+"_SPAttr"
    //val administrativeDivisionSpatialDF =
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

    val newNames = mutable.Buffer[String](dataSlice+"."+CoreRealmOperationUtil.RealmGlobalUID)
    dataSliceAttributes.foreach(attribute=>{
      newNames += (dataSlice+"."+attribute)
    })
    newNames += (geospatialScaleGrade+"."+CoreRealmOperationUtil.RealmGlobalUID)

    administrativeDivisionAttributes.foreach(attribute=>{
      newNames += (geospatialScaleGrade+"."+attribute)
    })
    val dfRenamed = calculateResultDF.toDF(newNames: _*)
    generateResultDataSet(dfRenamed.schema,dfRenamed.collect())
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

  private def generateResultDataSet(dataStructure:StructType,dataRowArray:Array[Row]): com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    val structureFields = dataStructure.fields
    val propertiesInfo = new java.util.HashMap[String,String]
    structureFields.foreach(item =>{
      propertiesInfo.put(item.name,item.dataType.typeName)
    })

    val dataList = new java.util.ArrayList[java.util.HashMap[String,Object]]
    dataRowArray.foreach(row=>{
      val currentMap = new java.util.HashMap[String,Object]
      dataList.add(currentMap)
      structureFields.foreach(fieldStructure=>{
        currentMap.put(fieldStructure.name,row.get(row.fieldIndex(fieldStructure.name)).asInstanceOf[AnyRef])
      })
    })

    new com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset(propertiesInfo,dataList)
  }
}
