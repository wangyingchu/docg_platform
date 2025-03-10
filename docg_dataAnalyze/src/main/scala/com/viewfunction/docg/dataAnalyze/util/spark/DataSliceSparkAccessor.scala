package com.viewfunction.docg.dataAnalyze.util.spark

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel.GeospatialScaleLevel
import org.apache.ignite.Ignition
import org.apache.ignite.spark.IgniteDataFrameSettings.{FORMAT_IGNITE, OPTION_CONFIG_FILE, OPTION_TABLE}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.ignite.IgniteSparkSession

class DataSliceSparkAccessor(private val sessionName:String, private val masterLocation:String,
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

  def getDataFrameWithSpatialSupportFromDataSlice(dataSliceName:String,geospatialLevel:GeospatialScaleLevel,dataFrameName:String,spatialAttributeName:String):DataFrame = {
    igniteSession.read
      .format(FORMAT_IGNITE) //Data source type.
      .option(OPTION_TABLE, dataSliceName) //Ignite table to read.
      .option(OPTION_CONFIG_FILE, CONFIG) //Ignite config.
      .load()

    var spatialConvertSQL = ""
    geospatialLevel match {
      case GeospatialScaleLevel.GlobalLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_GLGeometryContent) AS GL_Geometry FROM "+dataSliceName
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_GLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName
        }
      case GeospatialScaleLevel.CountryLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_CLGeometryContent) AS CL_Geometry FROM "+dataSliceName
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_CLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName
        }
      case GeospatialScaleLevel.LocalLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS LL_Geometry FROM "+dataSliceName
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName
        }
    }
    val targetDF = igniteSession.sql(spatialConvertSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }

  def getDataFrameFromSQL(dataFrameName:String,dataFrameSQL:String):DataFrame = {
    val targetDF = igniteSession.sql(dataFrameSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
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
