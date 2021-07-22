package analysisExample

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.util.coreRealm.JDBCResultSetConvertor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil

object GraphAnalysisExample {

  def main(args:Array[String]):Unit = {
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val connectionEdgeDF = globalDataAccessor.getDataFrameFromDataSlice("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup)
    val endPointDF = globalDataAccessor.getDataFrameFromDataSlice("MainlineEndPoint",CoreRealmOperationUtil.defaultSliceGroup)
    val mainlineDF = globalDataAccessor.getDataFrameFromDataSlice("PermittedUseMainline",CoreRealmOperationUtil.defaultSliceGroup)
    val middlePointDF = globalDataAccessor.getDataFrameFromDataSlice("MainlineConnectionPoint",CoreRealmOperationUtil.defaultSliceGroup)

    //println(connectionEdgeDF.count())
    //println(endPointDF.count())
    //println(mainlineDF.count())
    //println(middlePointDF.count())


    val result = globalDataAccessor._getJDBCRDD("GS_SpatialConnect",CoreRealmOperationUtil.defaultSliceGroup,new JDBCResultSetConvertor)



    println(result.count())






    globalDataAccessor.close()
  }

}
