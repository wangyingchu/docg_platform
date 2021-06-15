package globalDataAccessorTest

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil

object GlobalDataAccessorTestCase01 extends App{

  val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  val sparkExecutorInstanceNumber = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorInstanceNumber")
  val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation,sparkExecutorInstanceNumber)

  val wetLandMemoryTable = globalDataAccessor.getMemoryTable("Wetland")
  println(wetLandMemoryTable.getMemoryTableMetaInfo.getTotalDataCount)

  val targetDF = globalDataAccessor.getDataFrameFromMemoryTable("Wetland","defaultGroup")
  targetDF.printSchema()
  targetDF.take(50).foreach(println(_))
  targetDF.persist()

  Thread.sleep(5000)
  globalDataAccessor.close()
}
