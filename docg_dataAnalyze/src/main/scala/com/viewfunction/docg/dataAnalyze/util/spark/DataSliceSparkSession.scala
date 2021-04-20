package com.viewfunction.docg.dataAnalyze.util.spark

import org.apache.ignite.Ignition
import org.apache.ignite.spark.IgniteDataFrameSettings.{FORMAT_IGNITE, OPTION_CONFIG_FILE, OPTION_TABLE}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.ignite.IgniteSparkSession

class DataSliceSparkSession(private val sessionName:String,private val masterLocation:String,
                            private val executorInstanceNumber:String) {

  private val CONFIG = "configurations/dataCompute-ignite.xml"
  Ignition.setClientMode(true)
  //Creating Ignite-specific implementation of Spark session.
  val igniteSession = IgniteSparkSession.builder()
    .appName(sessionName)
    .master(masterLocation)
    .config("spark.executor.instances", executorInstanceNumber)
    .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
    .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
    .igniteConfig(CONFIG)
    .getOrCreate()
  //Register Sedona SQL functions
  SedonaSQLRegistrator.registerAll(igniteSession)

  def getDataFrameFromDataSlice(dataSliceName:String):DataFrame = {
    val igniteDF = igniteSession.read
      .format(FORMAT_IGNITE) //Data source type.
      .option(OPTION_TABLE, dataSliceName) //Ignite table to read.
      .option(OPTION_CONFIG_FILE, CONFIG) //Ignite config.
      .load()
    igniteDF
  }

  def getDataFrameFromSQL(dataFrameName:String,dataFrameSQL:String):DataFrame = {
    val targetDF = igniteSession.sql(dataFrameSQL.stripMargin)
    targetDF.createOrReplaceTempView(dataFrameName)
    targetDF
  }

  def getSparkSession():SparkSession = {
    igniteSession
  }

  def close():Unit={
    // Close IgniteContext on all workers.
    igniteSession.close()
  }
}
