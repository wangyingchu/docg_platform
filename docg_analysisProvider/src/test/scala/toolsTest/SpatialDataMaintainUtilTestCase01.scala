package toolsTest

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.analysisProvider.tools.dataMaintain.SpatialDataMaintainUtil

import java.io.File

object SpatialDataMaintainUtilTestCase01 {

  def main(args:Array[String]):Unit ={
    val spatialDataMaintainUtil = new SpatialDataMaintainUtil
    val shpFile = new File("/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Seattle_Streets/Seattle_Streets.shp")

    val shpParseResult = spatialDataMaintainUtil.parseSHPData(shpFile,null)

    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val sparkExecutorInstanceNumber = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorInstanceNumber")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation,sparkExecutorInstanceNumber)


    val targetMemoryTable = spatialDataMaintainUtil.duplicateSpatialDataInfoToMemoryTable(globalDataAccessor,shpParseResult,"Streets","defaultGroup",true,null)

    val memoryTableMetaInfo = targetMemoryTable.getMemoryTableMetaInfo

    println(memoryTableMetaInfo.getMemoryTableName)
    println(memoryTableMetaInfo.getMemoryTableGroupName)
    println(memoryTableMetaInfo.getPrimaryDataCount)

    Thread.sleep(3000)
    globalDataAccessor.close()
  }

}
