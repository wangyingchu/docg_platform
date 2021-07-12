package analysisExample

import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil

import java.util
import java.util.Map

object SpatialRelationExtractionExample extends App {


  //val zzz: CoreRealmOperationUtil = new com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil()



  val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
  dataSlicePropertyMap.put("MNLCP_DEPT",DataSlicePropertyType.STRING)
  dataSlicePropertyMap.put("MNLCP_FEA_",DataSlicePropertyType.INT)
  dataSlicePropertyMap.put("MNLCP_ROTA",DataSlicePropertyType.INT)
  dataSlicePropertyMap.put("MNLCP_CV_2",DataSlicePropertyType.DOUBLE)


  val mainlineConnectionPointDataSlice: DataSliceOperationResult =
    CoreRealmOperationUtil.syncConceptionKindToDataSlice("MainlineConnectionPoint", "MainlineConnectionPoint", CoreRealmOperationUtil.defaultSliceGroup,dataSlicePropertyMap)

  println(mainlineConnectionPointDataSlice.getOperationSummary)

  //zzz.syncConceptionKindToDataSlice("aaaa","aaaa","")


  //val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  //val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  //val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)


  //globalDataAccessor.close()

}
