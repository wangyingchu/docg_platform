import org.apache.ignite.Ignition
import org.apache.ignite.spark.IgniteDataFrameSettings.{FORMAT_IGNITE, OPTION_CONFIG_FILE, OPTION_TABLE}
import org.apache.log4j.{Level, Logger}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.ignite.IgniteSparkSession

object GeoDataExample extends App{

  private val CONFIG = "configurations/dataCompute-ignite.xml"
  loadIndividualTreeGeoData
  //loadRoadWeatherRecordsGeoData

  def loadIndividualTreeGeoData():Unit={
    Ignition.setClientMode(true)
    val ignite = Ignition.start(CONFIG)
    //Creating Ignite-specific implementation of Spark session.
    val igniteSession = IgniteSparkSession.builder()
      .appName("Spark Ignite catalog example")
      .master("local")
      .config("spark.executor.instances", "2")
      .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
      .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
      .igniteConfig(CONFIG)
      .getOrCreate()

    SedonaSQLRegistrator.registerAll(igniteSession)

    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

    val igniteDF = igniteSession.read
      .format(FORMAT_IGNITE) //Data source type.
      .option(OPTION_TABLE, "IndividualTree") //Table to read.
      .option(OPTION_CONFIG_FILE, CONFIG) //Ignite config.
      .load()

    println("Data frame schema:")
    igniteDF.printSchema() //Printing query schema to console.

    println("Data frame content:")
    igniteDF.show(100)

    val df = igniteSession.sql("SELECT * FROM IndividualTree WHERE SZ = '蓝花楹'")
    df.createOrReplaceTempView("IndividualTree_part")
    df.printSchema()
    println("Result content:")
    df.show()

    // DBU = diameter ot breast height(胸径)
    val spatialDf = igniteSession.sql(
      """
        |SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent)  AS treeLocation ,SZ as treeType ,XJ as treeDBH
        |FROM IndividualTree_part
    """.stripMargin)

    spatialDf.printSchema()
    spatialDf.show()

    igniteSession.catalog.listColumns("IndividualTree").show()
    ignite.close()
  }

  def loadRoadWeatherRecordsGeoData():Unit={
    Ignition.setClientMode(true)
    val ignite = Ignition.start(CONFIG)
    //Creating Ignite-specific implementation of Spark session.
    val igniteSession = IgniteSparkSession.builder()
      .appName("Spark Ignite catalog example")
      .master("local")
      .config("spark.executor.instances", "2")
      .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
      .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
      .igniteConfig(CONFIG)
      .getOrCreate()
    //Register Sedona SQL functions
    SedonaSQLRegistrator.registerAll(igniteSession)

    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

    val igniteDF = igniteSession.read
      .format(FORMAT_IGNITE) //Data source type.
      .option(OPTION_TABLE, "RoadWeatherRecords") //Table to read.
      .option(OPTION_CONFIG_FILE, CONFIG) //Ignite config.
      .load()

    println("Data frame schema:")
    igniteDF.printSchema() //Printing query schema to console.
    println("Data frame content:")
    igniteDF.show(100)

    val spatialDf = igniteSession.sql(
      """
        |SELECT ST_GeomFromWKT(stationLocation)  AS recordLocation , stationName
        |FROM RoadWeatherRecords
    """.stripMargin)

    spatialDf.printSchema()
    spatialDf.show()
    igniteSession.catalog.listColumns("RoadWeatherRecords").show()
    ignite.close()
  }

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