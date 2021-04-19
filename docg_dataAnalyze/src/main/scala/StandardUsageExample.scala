import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkSession

object StandardUsageExample extends App{

  DataSliceOperationUtil.turnOffSparkLog()
  DataSliceOperationUtil.turnOffDataSliceLog()

  val dataSliceSparkSession = new DataSliceSparkSession()

  try{


  }finally dataSliceSparkSession.close()
}
