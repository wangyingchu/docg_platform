import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkSession

object StandardUsageExample extends App{

  DataSliceOperationUtil.turnOffSparkLog()
  DataSliceOperationUtil.turnOffDataSliceLog()

  val dataSliceSparkSession = new DataSliceSparkSession("StandardUsageExample","local","10")
  try{
    val individualTreeDF = dataSliceSparkSession.getDataFrameFromDataSlice("IndividualTree")
    //individualTreeDF.show(100)
    individualTreeDF.printSchema()

    val customDf = dataSliceSparkSession.getSparkSession().sql("SELECT * FROM IndividualTree WHERE SZ = '蓝花楹'")
    customDf.createOrReplaceTempView("IndividualTree_FilterA")
    customDf.printSchema()
    //customDf.show(10)

    /*
    val spatialIndividualTreeDf = dataSliceSparkSession.getSparkSession().sql(
      """
        |SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent)  AS treeLocation ,SZ as treeType ,XJ as treeDBH, DMID as treeID
        |FROM IndividualTree_FilterA
      """.stripMargin)
    spatialIndividualTreeDf.createOrReplaceTempView("spatialIndividualTreeDf")
    spatialIndividualTreeDf.printSchema()
    */
    // DBU = diameter ot breast height(胸径)
    val spatialIndividualTreeDf = dataSliceSparkSession.getDataFrameFromSQL("spatialIndividualTreeDf",
      """
        |SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent)  AS treeLocation ,SZ as treeType ,XJ as treeDBH, DMID as treeID
        |FROM IndividualTree_FilterA
      """
    )
    spatialIndividualTreeDf.printSchema()

    val sectionBlockDF = dataSliceSparkSession.getDataFrameFromDataSlice("SectionBlock")
    sectionBlockDF.printSchema()

    val spatialSectionBlockDf = dataSliceSparkSession.getDataFrameFromSQL("spatialSectionBlockDf","SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS blockLocation , BKMC AS blockName FROM SectionBlock")
    spatialSectionBlockDf.printSchema()

    val spatialFunctionComputeDfQueryString = "SELECT * FROM spatialSectionBlockDf, spatialIndividualTreeDf WHERE ST_Contains(spatialSectionBlockDf.blockLocation,spatialIndividualTreeDf.treeLocation)";
    val spatialFunctionComputeDf = dataSliceSparkSession.getSparkSession().sql(spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf.show(1000)

  }finally dataSliceSparkSession.close()
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