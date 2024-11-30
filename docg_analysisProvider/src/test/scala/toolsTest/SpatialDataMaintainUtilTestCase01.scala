package toolsTest

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance.SpatialDataMaintainUtil
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil

import java.io.File

object SpatialDataMaintainUtilTestCase01 {

  def main(args:Array[String]):Unit ={
    testCase01()
    testCase02()
  }

  def testCase01():Unit={
    val spatialDataMaintainUtil = new SpatialDataMaintainUtil
    val shpFile = new File("/media/wangychu/NSStorage1/Dev_Data/GIS_DATA/SEATTLE_GIS_DATA/Transportation/Seattle_Streets/Seattle_Streets.shp")

    val shpParseResult = spatialDataMaintainUtil.parseSHPData(shpFile,null)

    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val targetMemoryTable = spatialDataMaintainUtil.duplicateSpatialDataInfoToDataSlice(globalDataAccessor.dataService,shpParseResult,"Streets","defaultGroup",true,null)

    val memoryTableMetaInfo = targetMemoryTable.getDataSliceMetaInfo

    println(memoryTableMetaInfo.getDataSliceName)
    println(memoryTableMetaInfo.getSliceGroupName)
    println(memoryTableMetaInfo.getPrimaryDataCount)

    Thread.sleep(3000)
    globalDataAccessor.close()
  }

  def testCase02():Unit={
    val spatialDataMaintainUtil = new SpatialDataMaintainUtil
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)
    spatialDataMaintainUtil.syncGeospatialRegionToDataSlice(globalDataAccessor.dataService)

    Thread.sleep(3000)
    globalDataAccessor.close()
  }
}
