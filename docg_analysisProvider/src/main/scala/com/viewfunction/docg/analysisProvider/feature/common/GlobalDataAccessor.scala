package com.viewfunction.docg.analysisProvider.feature.common

import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.{DataSlice, DataSliceServiceInvoker}
import org.apache.ignite.{Ignite, Ignition}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.lang.Boolean

class GlobalDataAccessor (private val sessionName:String, private val masterLocation:String){

  val isClientIgniteNode = Boolean.parseBoolean(AnalysisProviderApplicationUtil.getApplicationProperty("isClientIgniteNode"))
  Ignition.setClientMode(isClientIgniteNode)
  val igniteNode = Ignition.start("configurations/dataAnalysis-ignite.xml")
  val dataServiceInvoker = DataSliceServiceInvoker.getInvokerInstance(igniteNode)

  val sparkCoresMax = AnalysisProviderApplicationUtil.getApplicationProperty("sparkCoresMax")
  val sparkExecutorCores = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorCores")
  val sparkExecutorMemory = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorMemory")
  val sparkMemoryOffHeapEnabled = Boolean.parseBoolean(AnalysisProviderApplicationUtil.getApplicationProperty("sparkMemoryOffHeapEnabled"))
  val sparkMemoryOffHeapSize = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMemoryOffHeapSize")

  val sparkSession : SparkSession = SparkSession.builder.appName(sessionName).master(masterLocation)
    //.config("spark.default.parallelism","200")

    .config("spark.cores.max",sparkCoresMax)
    .config("spark.executor.cores",sparkExecutorCores)
    .config("spark.executor.memory",sparkExecutorMemory)
    .config("spark.memory.offHeap.enabled",sparkMemoryOffHeapEnabled)
    .config("spark.memory.offHeap.size",sparkMemoryOffHeapSize)

    .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
    .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
    .getOrCreate()
  //Register Sedona SQL functions
  SedonaSQLRegistrator.registerAll(sparkSession)

  def getDataFrameFromDataSlice(sliceName: String, sliceGroup: String):DataFrame={
    val jdbcURL: String = if (sliceGroup != null) {
      "jdbc:ignite:thin://127.0.0.1/"+sliceGroup+"?partitionAwareness=true"
    }else {
      "jdbc:ignite:thin://127.0.0.1/?partitionAwareness=true"
    }
     val df = getSparkSession().sqlContext.read.format("jdbc")
      .option("url", jdbcURL)
      .option("driver", "org.apache.ignite.IgniteJdbcThinDriver")
      .option("dbtable", sliceName)
      .option("fetchSize",10000)
      .load()
    df
  }

  def getDataFrameWithSpatialSupportFromDataSlice(dataSliceName:String,sliceGroup: String,spatialValueName:String,dataFrameName:String,spatialAttributeName:String):DataFrame = {
    val orgDataFrame = getDataFrameFromDataSlice(dataSliceName,sliceGroup)
    orgDataFrame.createOrReplaceTempView(dataSliceName)

    val spatialConvertSQL = "SELECT * , ST_GeomFromWKT("+spatialValueName+") AS "+spatialAttributeName+" FROM "+dataSliceName
    val targetDF = getSparkSession().sql(spatialConvertSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }


/*
  def getDataFrameWithSpatialSupportFromDataSlice(dataSliceName:String,sliceGroup: String,geospatialLevel:GeospatialScaleLevel,dataFrameName:String,spatialAttributeName:String):DataFrame = {
    val orginalDataFrame = getDataFrameFromDataSlice(dataSliceName,sliceGroup)
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
    val targetDF = _getSparkSession().sql(spatialConvertSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }
*/

  def getDataSlice(dataSliceName:String): DataSlice = {
    _getDataSliceServiceInvoker().getDataSlice(dataSliceName)
  }

  def close():Unit={
    sparkSession.close()
    igniteNode.close()
  }

  def getSparkSession(): SparkSession = {
    sparkSession
  }

  def _getIgniteNode(): Ignite = {
    igniteNode
  }

  def _getDataSliceServiceInvoker():DataSliceServiceInvoker = {
    dataServiceInvoker
  }

  def _getDataFrameFromSparkSQL(dataFrameName:String, dataFrameSQL:String):DataFrame = {
    val targetDF = sparkSession.sql(dataFrameSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }
}
