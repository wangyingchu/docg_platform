package analysisExample

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.spark.spatial.{SpatialPredicateType, SpatialQueryMetaFunction, SpatialQueryParam}
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import scala.collection.mutable

object SpatialRelationExtractionExample extends App {

  /*
  loadMainlineConnectionPointDS
  loadMainlineEndPointDS
  loadPermittedUseMainlineDS
  */

  spatialRelationExtract

  def spatialRelationExtract():Unit = {
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val mainlineConnectionPointSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice("MainlineConnectionPoint",CoreRealmOperationUtil.defaultSliceGroup, RealmConstant._GeospatialGLGeometryContent,"mainlineConnectionPointSpDF","geo_ConnectionPointLocation")
    //mainlineConnectionPointSpDF.printSchema()
    //mainlineConnectionPointSpDF.show(50)

    val mainlineEndPointSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice("MainlineEndPoint",CoreRealmOperationUtil.defaultSliceGroup, RealmConstant._GeospatialGLGeometryContent,"mainlineEndPointSpDF","geo_EndPointLocation")
    //mainlineEndPointSpDF.printSchema()
    //mainlineEndPointSpDF.show(50)

    val permittedUseMainlineSpDF = globalDataAccessor.getDataFrameWithSpatialSupportFromDataSlice("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup, RealmConstant._GeospatialGLGeometryContent,"permittedUseMainlineSpDF","geo_LineLocation")
    //permittedUseMainlineSpDF.printSchema()
    //permittedUseMainlineSpDF.show(50)

    val spatialQueryMetaFunction = new SpatialQueryMetaFunction

    val mainlineEndPoint_spatialQueryParam = SpatialQueryParam("mainlineEndPointSpDF","geo_EndPointLocation",mutable.Buffer[String]("REALMGLOBALUID"))
    val permittedUseMainline_spatialQueryParam = SpatialQueryParam("permittedUseMainlineSpDF","geo_LineLocation",mutable.Buffer[String]("REALMGLOBALUID"))
    val mainlineEndPoint_permittedUseMainlineJoinDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,mainlineEndPoint_spatialQueryParam,SpatialPredicateType.Touches,permittedUseMainline_spatialQueryParam,"mainlineEndPoint_permittedUseMainlineJoinDF")
    //mainlineEndPoint_permittedUseMainlineJoinDF.show(100)
    //println(mainlineEndPoint_permittedUseMainlineJoinDF.count)

    val mainlineConnectionPoint_spatialQueryParam = SpatialQueryParam("mainlineConnectionPointSpDF","geo_ConnectionPointLocation",mutable.Buffer[String]("REALMGLOBALUID"))
    val mainlineConnectionPoint_permittedUseMainlineJoinDF = spatialQueryMetaFunction.spatialJoinQuery(globalDataAccessor,mainlineConnectionPoint_spatialQueryParam,SpatialPredicateType.Overlaps,permittedUseMainline_spatialQueryParam,"mainlineEndPoint_permittedUseMainlineJoinDF")
    //mainlineConnectionPoint_permittedUseMainlineJoinDF.show(100)

    val schemas= Seq("uid0", "uid1")
    val dfRenamed = mainlineEndPoint_permittedUseMainlineJoinDF.toDF(schemas: _*)

    println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date))
    dfRenamed.write.csv("/home/wangychu/Desktop/csvoutput/01")
    println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date))

    globalDataAccessor.close()
  }

  def loadMainlineConnectionPointDS():Unit = {
    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertyMap.put("MNLCP_DEPT",DataSlicePropertyType.STRING)
    dataSlicePropertyMap.put("MNLCP_FEA_",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNLCP_ROTA",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNLCP_CV_2",DataSlicePropertyType.DOUBLE)
    val mainlineConnectionPointDataSlice: DataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("MainlineConnectionPoint", "MainlineConnectionPoint", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap,CoreRealmOperationUtil.GeospatialScaleLevel.GlobalLevel)
    println(mainlineConnectionPointDataSlice.getSuccessItemsCount)
  }

  def loadMainlineEndPointDS():Unit = {
    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertyMap.put("MNLEP_FE_1",DataSlicePropertyType.STRING)
    dataSlicePropertyMap.put("MNLEP_FEA_",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNLEP_X_CO",DataSlicePropertyType.DOUBLE)
    dataSlicePropertyMap.put("MNLEP_Y_CO",DataSlicePropertyType.DOUBLE)
    dataSlicePropertyMap.put("MNLEP_ELEV",DataSlicePropertyType.DOUBLE)
    val mainlineEndPointDataSlice: DataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("MainlineEndPoint", "MainlineEndPoint", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap,CoreRealmOperationUtil.GeospatialScaleLevel.GlobalLevel)
    println(mainlineEndPointDataSlice.getSuccessItemsCount)
  }

  def loadPermittedUseMainlineDS():Unit = {
    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertyMap.put("MNL_LENGTH",DataSlicePropertyType.DOUBLE)
    dataSlicePropertyMap.put("MNL_FCNLEN",DataSlicePropertyType.DOUBLE)
    dataSlicePropertyMap.put("MNL_DNS_FE",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNL_PIPE_1",DataSlicePropertyType.STRING)
    dataSlicePropertyMap.put("MNL_LIFE_1",DataSlicePropertyType.STRING)
    val mainlineEndPointDataSlice: DataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("PermittedUseMainline", "PermittedUseMainline", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap,CoreRealmOperationUtil.GeospatialScaleLevel.GlobalLevel)
    println(mainlineEndPointDataSlice.getSuccessItemsCount)
  }

}
