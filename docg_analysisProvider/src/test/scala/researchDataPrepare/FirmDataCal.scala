package researchDataPrepare

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset
import com.viewfunction.docg.analysisProvider.feature.functionalFeatures.AdministrativeDivisionBasedSpatialAnalysis
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance.SpatialDataMaintainUtil
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
    val calculateResult : ResponseDataset = AdministrativeDivisionBasedSpatialAnalysis.executeDataSliceAdministrativeDivisionSpatialCalculation(
      globalDataAccessor,"firmData","defaultGroup",
      mutable.Buffer[String]("name"),
      SpatialPredicateType.Within,
      com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.COUNTY,
      mutable.Buffer[String]("DOCG_GEOSPATIALCODE","DOCG_GEOSPATIALCHINESENAME"),
      GeospatialScaleLevel.CountryLevel,0.0001
    )

    println(calculateResult.getPropertiesInfo)
    println( calculateResult.getDataList.size)
    println( calculateResult.getDataList.get(500))

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
