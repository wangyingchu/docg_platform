import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor

object SpatialComputeExample extends App{

  DataSliceOperationUtil.turnOffSparkLog()
  DataSliceOperationUtil.turnOffDataSliceLog()

  val dataSliceSparkAccessor = new DataSliceSparkAccessor("StandardUsageExample","local","10")
  try{
    val sectionBlockDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("SectionBlock",GeospatialScaleLevel.LocalLevel,"sectionBlockSTDF")
    sectionBlockDF.show(10)
    sectionBlockDF.printSchema()

    val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("IndividualTree",GeospatialScaleLevel.LocalLevel,"individualTreeSTDF")
    individualTreeDF.show(10)
    individualTreeDF.printSchema()

    val spatialFunctionComputeDfQueryString = "SELECT * FROM sectionBlockSTDF, individualTreeSTDF WHERE ST_Contains(sectionBlockSTDF.LL_Geometry,individualTreeSTDF.LL_Geometry)";
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(null,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf.show(10)

    println(spatialFunctionComputeDf.count())

  }finally dataSliceSparkAccessor.close()
}
