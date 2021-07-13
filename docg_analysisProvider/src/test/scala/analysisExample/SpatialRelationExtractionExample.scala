package analysisExample

import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil

import java.util

object SpatialRelationExtractionExample extends App {

  loadMainlineConnectionPointDS
  loadMainlineEndPointDS
  loadPermittedUseMainlineDS
  //val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  //val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  //val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)
  //globalDataAccessor.close()


  def loadMainlineConnectionPointDS():Unit = {
    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertyMap.put("MNLCP_DEPT",DataSlicePropertyType.STRING)
    dataSlicePropertyMap.put("MNLCP_FEA_",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNLCP_ROTA",DataSlicePropertyType.INT)
    dataSlicePropertyMap.put("MNLCP_CV_2",DataSlicePropertyType.DOUBLE)
    val mainlineConnectionPointDataSlice: DataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("MainlineConnectionPoint", "MainlineConnectionPoint", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap)
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
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("MainlineEndPoint", "MainlineEndPoint", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap)
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
      CoreRealmOperationUtil.syncConceptionKindToDataSlice("PermittedUseMainline", "PermittedUseMainline", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap)
    println(mainlineEndPointDataSlice.getSuccessItemsCount)
  }

}
