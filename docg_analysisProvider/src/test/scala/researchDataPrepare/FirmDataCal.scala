package researchDataPrepare

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.AdministrativeDivisionBasedSpatialAnalysis
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance.SpatialDataMaintainUtil
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.GeospatialScaleLevel
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion.GeospatialScaleGrade
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlicePropertyType}

import java.util
import scala.collection.mutable

object FirmDataCal extends App{

  val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)
  val dataServiceInvoker = globalDataAccessor._getDataSliceServiceInvoker()
  //val dataServiceInvoker = DataServiceInvoker.getInvokerInstance()
  val spatialDataMaintainUtil = new SpatialDataMaintainUtil
  val spatialQueryMetaFunction = new SpatialQueryMetaFunction

  try{
    //loadFirmDataSlice
    //loadSpatialDataSlice
    //calculateFirmLocation
    calculateFirmLocation2
  }catch{
    case e : Exception =>
      e.printStackTrace()
      globalDataAccessor.close()
  }
  globalDataAccessor.close()

  def calculateFirmLocation():Unit = {
    val firmLocationDF =
      globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice("firmData","defaultGroup", RealmConstant._GeospatialCLGeometryContent,"firmLocationDF","geo_FirmLocation")

    val spatialCountyDF =
      globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(SpatialAnalysisConstant.GeospatialScaleCountyDataSlice,"defaultGroup", RealmConstant._GeospatialCLGeometryContent,"spatialCountyDF","geo_CountyArea")

    val sampledFirmDF = firmLocationDF.sample(0.01)
    sampledFirmDF.createOrReplaceTempView("sampledFirmDF")

    //val firmLocationPoint_spatialQueryParam = spatial.SpatialQueryParam("firmLocationDF","geo_FirmLocation",mutable.Buffer[String]("REALMGLOBALUID","NAME"))
    val firmLocationPoint_spatialQueryParam = spatial.SpatialQueryParam("sampledFirmDF","geo_FirmLocation",mutable.Buffer[String]("REALMGLOBALUID","NAME"))
    val spatialCountyArea_spatialQueryParam = spatial.SpatialQueryParam("spatialCountyDF","geo_CountyArea",mutable.Buffer[String]("REALMGLOBALUID","DOCG_GEOSPATIALCODE","DOCG_GEOSPATIALCHINESENAME"))

    val calculateResultDF =
      spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,firmLocationPoint_spatialQueryParam,SpatialPredicateType.Within,spatialCountyArea_spatialQueryParam,"firm_CountyJoinDF")
    println(calculateResultDF.count())
  }

  def calculateFirmLocation2():Unit = {
    AdministrativeDivisionBasedSpatialAnalysis.executeDataSliceAdministrativeDivisionSpatialCalculation(
      globalDataAccessor,"firmData","defaultGroup",
      SpatialPredicateType.Within,
      com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.COUNTY,
      GeospatialScaleLevel.CountryLevel
    )
  }

  def loadFirmDataSlice():Unit={
    val syncPropertiesMapping = new util.HashMap[String,DataSlicePropertyType]
    syncPropertiesMapping.put("name",DataSlicePropertyType.STRING)
    val resultDataSlice =
      spatialDataMaintainUtil.syncGeospatialConceptionKindToDataSlice(dataServiceInvoker,
        "Firm","firmData","defaultGroup",syncPropertiesMapping,GeospatialScaleLevel.CountryLevel)
    println(resultDataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(resultDataSlice.getDataSliceMetaInfo.getTotalDataCount)
  }

  def loadSpatialDataSlice():Unit={
    spatialDataMaintainUtil.syncGeospatialRegionToDataSlice(dataServiceInvoker)
    val _CONTINENT_DataSlice = spatialDataMaintainUtil.getGeospatialRegionDataSlice(dataServiceInvoker,GeospatialScaleGrade.CONTINENT)
    println(_CONTINENT_DataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(_CONTINENT_DataSlice.getDataSliceMetaInfo.getTotalDataCount)
    val _VILLAGE_DataSlice = spatialDataMaintainUtil.getGeospatialRegionDataSlice(dataServiceInvoker,GeospatialScaleGrade.VILLAGE)
    println(_VILLAGE_DataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(_VILLAGE_DataSlice.getDataSliceMetaInfo.getTotalDataCount)
  }
}
