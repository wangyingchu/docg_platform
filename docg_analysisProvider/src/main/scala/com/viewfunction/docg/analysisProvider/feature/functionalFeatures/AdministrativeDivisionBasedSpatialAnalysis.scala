package com.viewfunction.docg.analysisProvider.feature.functionalFeatures

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleGrade, GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant

import scala.collection.mutable

object AdministrativeDivisionBasedSpatialAnalysis {

  def executeDataSliceAdministrativeDivisionSpatialCalculation(globalDataAccessor:GlobalDataAccessor,
                                                               dataSlice:String,sliceGroup:String,
                                                               spatialPredicateType:SpatialPredicateType,
                                                               geospatialScaleGrade:GeospatialScaleGrade,
                                                               geospatialScaleLevel:GeospatialScaleLevel):Unit = {
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

    val dataSlice_spatialQueryParam = spatial.SpatialQueryParam(dataSliceSpatialDFName,dataSliceSpatialAttributeName,
      mutable.Buffer[String]("REALMGLOBALUID","NAME"))
    val administrativeDivision_spatialQueryParam = spatial.SpatialQueryParam(administrativeDivisionSpatialDFName,administrativeDivisionSpatialAttributeName,
      mutable.Buffer[String]("REALMGLOBALUID","DOCG_GEOSPATIALCODE","DOCG_GEOSPATIALCHINESENAME"))

    val spatialQueryMetaFunction = new SpatialQueryMetaFunction
    val calculateResultDFName = "calculateResultJoinDF"
    val calculateResultDF =
      spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,dataSlice_spatialQueryParam,spatialPredicateType,administrativeDivision_spatialQueryParam,calculateResultDFName)

    calculateResultDF.take(100).foreach( item=> {
      println(item)
    })





  }



  def getGeospatialGeometryContent(geospatialScaleLevel:GeospatialScaleLevel):String = {
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

  def getAdministrativeDivisionDataSliceName(geospatialScaleGrade:GeospatialScaleGrade):String = {
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
