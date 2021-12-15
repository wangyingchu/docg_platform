package researchDataPrepare

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial
import com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial.SpatialQueryMetaFunction
import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance.SpatialDataMaintainUtil
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{GeospatialScaleLevel, SpatialAnalysisConstant, SpatialPredicateType}
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion.GeospatialScaleGrade
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlicePropertyType}
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil

import java.util
import scala.collection.mutable

object FirmDataCal extends App{


  val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

  val dataServiceInvoker = globalDataAccessor._getDataSliceServiceInvoker()
  //val dataServiceInvoker = DataServiceInvoker.getInvokerInstance()
  val spatialDataMaintainUtil = new SpatialDataMaintainUtil
  try{
    //loadFirmDataSlice(dataServiceInvoker,spatialDataMaintainUtil)
    //loadSpatialDataSlice(dataServiceInvoker,spatialDataMaintainUtil)
    calculateFirmLocation(dataServiceInvoker)
  }catch{
    case e : Exception =>
      e.printStackTrace()
      globalDataAccessor.close()
  }

  globalDataAccessor.close()

  def calculateFirmLocation(dataServiceInvoker:DataServiceInvoker):Unit = {
    //val firmDataSlice = dataServiceInvoker.getDataSlice("firmData")
    //val spatialCountyDataSlice = dataServiceInvoker.getDataSlice(SpatialAnalysisConstant.GeospatialScaleCountyDataSlice)

    //println(firmDataSlice.getDataSliceMetaInfo.getTotalDataCount)
    //println(spatialCountyDataSlice.getDataSliceMetaInfo.getTotalDataCount)

    val firmLocationDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice("firmData","defaultGroup", RealmConstant._GeospatialCLGeometryContent,"firmLocationDF","geo_FirmLocation")
    //firmLocationDF.printSchema()
    //firmLocationDF.show(10)
    //println(firmLocationDF.count())

    val spatialCountyDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(SpatialAnalysisConstant.GeospatialScaleCountyDataSlice,"defaultGroup", RealmConstant._GeospatialCLGeometryContent,"spatialCountyDF","geo_CountyArea")
    //val spatialCountyDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice(SpatialAnalysisConstant.GeospatialScaleProvinceDataSlice,"defaultGroup", RealmConstant._GeospatialCLGeometryContent,"spatialCountyDF","geo_CountyArea")
    //spatialCountyDF.printSchema()
    //spatialCountyDF.show(10)
    //println(spatialCountyDF.count())

    val spatialQueryMetaFunction = new SpatialQueryMetaFunction
    val firmLocationPoint_spatialQueryParam = spatial.SpatialQueryParam("firmLocationDF","geo_FirmLocation",mutable.Buffer[String]("REALMGLOBALUID","NAME"))
    val spatialCountyArea_spatialQueryParam = spatial.SpatialQueryParam("spatialCountyDF","geo_CountyArea",mutable.Buffer[String]("REALMGLOBALUID","DOCG_GEOSPATIALCODE","DOCG_GEOSPATIALCHINESENAME"))

    val calculateResultDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,firmLocationPoint_spatialQueryParam,SpatialPredicateType.Within,spatialCountyArea_spatialQueryParam,"mainlineEndPoint_permittedUseMainlineJoinDF")
    calculateResultDF.printSchema()
    //calculateResultDF.show(1000)
    println(calculateResultDF.count())
  }

  def loadFirmDataSlice(dataServiceInvoker:DataServiceInvoker,spatialDataMaintainUtil:SpatialDataMaintainUtil):Unit={
    val syncPropertiesMapping = new util.HashMap[String,DataSlicePropertyType]
    syncPropertiesMapping.put("name",DataSlicePropertyType.STRING)
    val resultDataSlice =
      spatialDataMaintainUtil.syncGeospatialConceptionKindToDataSlice(dataServiceInvoker,
        "Firm","firmData","defaultGroup",syncPropertiesMapping,GeospatialScaleLevel.CountryLevel)
    println(resultDataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(resultDataSlice.getDataSliceMetaInfo.getTotalDataCount)
  }

  def loadSpatialDataSlice(dataServiceInvoker:DataServiceInvoker,spatialDataMaintainUtil:SpatialDataMaintainUtil):Unit={
    spatialDataMaintainUtil.syncGeospatialRegionToDataSlice(dataServiceInvoker,"defaultGroup")
    val _CONTINENT_DataSlice = spatialDataMaintainUtil.getGeospatialRegionDataSlice(dataServiceInvoker,GeospatialScaleGrade.CONTINENT)
    println(_CONTINENT_DataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(_CONTINENT_DataSlice.getDataSliceMetaInfo.getTotalDataCount)
    val _VILLAGE_DataSlice = spatialDataMaintainUtil.getGeospatialRegionDataSlice(dataServiceInvoker,GeospatialScaleGrade.VILLAGE)
    println(_VILLAGE_DataSlice.getDataSliceMetaInfo.getDataSliceName)
    println(_VILLAGE_DataSlice.getDataSliceMetaInfo.getTotalDataCount)
  }
}
