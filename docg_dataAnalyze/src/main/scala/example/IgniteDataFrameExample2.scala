package example

import org.apache.ignite.Ignition
import org.apache.ignite.spark.IgniteDataFrameSettings.{FORMAT_IGNITE, OPTION_CONFIG_FILE, OPTION_TABLE}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.ignite.IgniteSparkSession

object IgniteDataFrameExample2 extends App {
  private val CONFIG = "configurations/dataCompute-ignite.xml"
  sparkDateFrameAccessMethod1(CONFIG)
  sparkDateFrameAccessMethod2(CONFIG)

  def sparkDateFrameAccessMethod1(CONFIG:String):Unit={
    Ignition.setClientMode(true)
    val ignite = Ignition.start(CONFIG)

    //Creating Ignite-specific implementation of Spark session.
    val igniteSession = IgniteSparkSession.builder()
      .appName("Spark Ignite catalog example")
      .master("local")
      .config("spark.executor.instances", "2")
      .igniteConfig(CONFIG)
      .getOrCreate()

    //Adjust the logger to exclude the logs of no interest.
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

    val df = igniteSession.sql("SELECT * FROM RoadWeatherRecords WHERE airTEMPERATURE > 48.0")
    df.printSchema()
    println("Result content:")
    df.show()

    igniteSession.catalog.listColumns("RoadWeatherRecords").show()

    ignite.close()
  }

  def sparkDateFrameAccessMethod2(CONFIG:String):Unit={
    //Starting Ignite server node.
    Ignition.setClientMode(true)
    val ignite = Ignition.start(CONFIG)

    //Creating spark session.
    implicit val spark = SparkSession.builder()
      .appName("Spark Ignite data sources example")
      .master("local")
      .config("spark.executor.instances", "2")
      .getOrCreate()
    // Adjust the logger to exclude the logs of no interest.
    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

    val igniteDF = spark.read
      .format(FORMAT_IGNITE) //Data source type.
      .option(OPTION_TABLE, "RoadWeatherRecords") //Table to read.
      .option(OPTION_CONFIG_FILE, CONFIG) //Ignite config.
      .load()

    println("Data frame schema:")
    igniteDF.printSchema() //Printing query schema to console.

    println("Data frame content:")
    igniteDF.show(5000)

    ignite.close()
  }
}