package example

import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}

import scala.collection.mutable

object StandardUsageExample extends App{

  DataSliceOperationUtil.turnOffSparkLog()
  DataSliceOperationUtil.turnOffDataSliceLog()

  val dataSliceSparkAccessor = new DataSliceSparkAccessor("StandardUsageExample","local","10")
  try{
    val individualTreeDF = dataSliceSparkAccessor.getDataFrameFromDataSlice("IndividualTree")
    individualTreeDF.show(10)
    individualTreeDF.printSchema()

    val customDf = dataSliceSparkAccessor.getSparkSession().sql("SELECT * FROM IndividualTree WHERE SZ = '蓝花楹'")
    customDf.createOrReplaceTempView("IndividualTree_FilterA")
    customDf.printSchema()
    customDf.show(10)

    // DBU = diameter ot breast height(胸径)
    val spatialIndividualTreeDf = dataSliceSparkAccessor.getDataFrameFromSQL("spatialIndividualTreeDf",
      """
        |SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent)  AS treeLocation ,SZ as treeType ,XJ as treeDBH, DMID as treeID
        |FROM IndividualTree_FilterA
      """
    )
    spatialIndividualTreeDf.printSchema()

    val sectionBlockDF = dataSliceSparkAccessor.getDataFrameFromDataSlice("SectionBlock")
    sectionBlockDF.printSchema()

    val spatialSectionBlockDf = dataSliceSparkAccessor.getDataFrameFromSQL("spatialSectionBlockDf","SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS blockLocation , BKMC AS blockName , RealmGlobalUID AS blockUID FROM SectionBlock")
    spatialSectionBlockDf.printSchema()

    val spatialFunctionComputeDfQueryString = "SELECT *,spatialIndividualTreeDf.treeDBH AS renamedDBH FROM spatialSectionBlockDf, spatialIndividualTreeDf WHERE ST_Contains(spatialSectionBlockDf.blockLocation,spatialIndividualTreeDf.treeLocation)";
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(null,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf.show(10)
    println(spatialFunctionComputeDf.count())

    val spatialQueryOperator = new SpatialQueryOperator()

    val spatialQueryParamA = SpatialQueryParam("spatialSectionBlockDf","blockLocation",null)
    val spatialQueryParamB = SpatialQueryParam("spatialIndividualTreeDf","treeLocation",null)

    val spatialFunctionComputeDf2 = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,spatialQueryParamB,SpatialPredicateType.Contains,spatialQueryParamA,null)
    spatialFunctionComputeDf2.show(10)
    println(spatialFunctionComputeDf2.count())

    val spatialFunctionComputeDf3 = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,spatialQueryParamB,SpatialPredicateType.Overlaps,spatialQueryParamA,null)
    spatialFunctionComputeDf3.show(10)
    println(spatialFunctionComputeDf3.count())

    val spatialFunctionComputeDf4 = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,spatialQueryParamB,SpatialPredicateType.Touches,spatialQueryParamA,null)
    spatialFunctionComputeDf4.show(10)
    println(spatialFunctionComputeDf4.count())

  }finally dataSliceSparkAccessor.close()
}
/* DataSlicesSyncKindList example config
ConceptionKind.RoadWeatherRecords
    Attribute.stationName    STRING
    Attribute.stationLocation    STRING
    Attribute.dateTime    DATE
    Attribute.RecordId    STRING
    Attribute.RoadSurfaceTemperature    DOUBLE
    Attribute.AirTemperature    DOUBLE
ConceptionKind.Ingredient
    Attribute.category    STRING
    Attribute.name    STRING
    Attribute.id    STRING
ConceptionKind.Compound
    Attribute.CASNumber    STRING
    Attribute.name    STRING
    Attribute.id    STRING
RelationKind.belongsToCategory
    Attribute.dataOrigin    STRING
RelationKind.isUsedIn
    Attribute.createDate    STRING
ConceptionKind.IndividualTree
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.GNQHID    STRING
    Attribute.DMID    STRING
    Attribute.SZ    STRING
    Attribute.XJ    STRING
ConceptionKind.Frutex
    Attribute.DOCG_GS_LLGeometryContent    STRING
ConceptionKind.FunctionalZone
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.GNQHID    STRING
    Attribute.GNQMC    STRING
ConceptionKind.ZoneSection
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.GNQHID    STRING
    Attribute.BKMC    STRING
    Attribute.GNQBH    STRING
ConceptionKind.SectionBlock
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.BKMC    STRING
    Attribute.GNQHID    STRING
    Attribute.Ymax    STRING
    Attribute.BH    STRING
    Attribute.BKBH    STRING
    Attribute.XFLX    STRING
    Attribute.XBLX    STRING
    Attribute.XBMC    STRING
ConceptionKind.Road
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.Attribute.XBLX    STRING
    Attribute.GNQ    STRING
    Attribute.BK    STRING
ConceptionKind.Building
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.Attribute.XBLX    STRING
    Attribute.GNQ    STRING
    Attribute.BK    STRING
ConceptionKind.ConstructionLand
    Attribute.DOCG_GS_LLGeometryContent    STRING
    Attribute.Attribute.XBLX    STRING
    Attribute.GNQ    STRING
    Attribute.BK    STRING
 */