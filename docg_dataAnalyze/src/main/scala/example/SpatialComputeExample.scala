package example

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}

import scala.collection.mutable

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

    val spatialQueryOperator = new SpatialQueryOperator()
    val spatialQueryParamC = SpatialQueryParam("sectionBlockSTDF","LL_Geometry",mutable.Buffer[String]("BH"))
    val spatialQueryParamD = SpatialQueryParam("individualTreeSTDF","LL_Geometry",mutable.Buffer[String]("DMID"))

    val spatialFunctionComputeDf5 = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,spatialQueryParamD,SpatialPredicateType.Within,spatialQueryParamC,null)
    spatialFunctionComputeDf5.show(6)
    println(spatialFunctionComputeDf5.count())

  }finally dataSliceSparkAccessor.close()
}
