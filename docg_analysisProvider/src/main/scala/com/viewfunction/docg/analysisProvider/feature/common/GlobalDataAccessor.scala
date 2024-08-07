package com.viewfunction.docg.analysisProvider.feature.common

import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.{CountryLevel, GeospatialScaleLevel, GlobalLevel, LocalLevel}
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.{ComputeGrid, DataService, DataSlice}
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.sql.{DriverManager, ResultSet}

class GlobalDataAccessor (private val sessionName:String, private val masterLocation:String){

  val computeGrid:ComputeGrid = ComputeGridTermFactory.getComputeGrid
  val dataService:DataService = computeGrid.getDataService
  val sparkExecutorCores = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorCores")
  val sparkDefaultParallelism = AnalysisProviderApplicationUtil.getApplicationProperty("sparkDefaultParallelism")
  val sparkExecutorMemory = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorMemory")
  val sparkMemoryOffHeapEnabled = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMemoryOffHeapEnabled")
  val sparkMemoryOffHeapSize = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMemoryOffHeapSize")
  val analysisProviderSparkRuntimeJarLocation = AnalysisProviderApplicationUtil.getApplicationProperty("analysisProviderSparkRuntimeJarLocation")
  val sedonaJoinNumPartition = AnalysisProviderApplicationUtil.getApplicationProperty("sedonaJoinNumPartition")

  val sparkConfig = new SparkConf
  sparkConfig.setMaster(masterLocation)
  sparkConfig.setAppName(sessionName)

  if(masterLocation.startsWith("spark://")){
    //use spark cluster,need set analysis Provider runtime jar location
    sparkConfig.setJars(Array[String]{analysisProviderSparkRuntimeJarLocation})
  }
  sparkConfig.set("spark.executor.cores",sparkExecutorCores)
  sparkConfig.set("spark.default.parallelism",sparkDefaultParallelism)
  sparkConfig.set("spark.executor.memory",sparkExecutorMemory)
  sparkConfig.set("spark.memory.offHeap.enabled",sparkMemoryOffHeapEnabled)
  sparkConfig.set("spark.memory.offHeap.size",sparkMemoryOffHeapSize)
  //set up for sedona :http://sedona.apache.org/download/cluster/
  sparkConfig.set("spark.driver.memory","10G")
  sparkConfig.set("spark.network.timeout","1000S")
  sparkConfig.set("spark.driver.maxResultSize","5G")
  //set up for sedona :http://sedona.apache.org/api/sql/Parameter/#usage
  sparkConfig.set("sedona.join.numpartition",sedonaJoinNumPartition) //use this configuration to improve Spatial Join query performance enormously
  sparkConfig.set("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
  sparkConfig.set("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
  sparkConfig.set("spark.kryoserializer.buffer.max","2047")

  val sc = new SparkContext(sparkConfig)
  val sparkSession = SparkSession.builder.config(sc.getConf).getOrCreate()

  /*
  val sparkSession : SparkSession = SparkSession.builder.appName(sessionName).master(masterLocation)
    //.config("spark.default.parallelism","200")
    .config("spark.cores.max",sparkCoresMax)
    .config("spark.executor.cores",sparkExecutorCores)
    .config("spark.executor.memory",sparkExecutorMemory)
    .config("spark.memory.offHeap.enabled",sparkMemoryOffHeapEnabled)
    .config("spark.memory.offHeap.size",sparkMemoryOffHeapSize)
    //set up for sedona :http://sedona.apache.org/download/cluster/
    .config("spark.driver.memory","10G")
    .config("spark.network.timeout","1000S")
    .config("spark.driver.maxResultSize","5G")
    .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
    .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
    .getOrCreate()
  */

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

    val spatialConvertSQL = "SELECT * , ST_GeomFromWKT("+spatialValueName+") AS "+spatialAttributeName+" FROM "+dataSliceName + " WHERE "+spatialValueName+" IS NOT NULL"
    val targetDF = getSparkSession().sql(spatialConvertSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }

  def getDataFrameWithSpatialSupportFromDataSlice(dataSliceName:String,sliceGroup: String,geoSpatialLevel:GeospatialScaleLevel,dataFrameName:String,spatialAttributeName:String):DataFrame = {
    var spatialConvertSQL = ""
    geoSpatialLevel match {
      case GlobalLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_GLGeometryContent) AS GL_Geometry FROM "+dataSliceName+ " WHERE DOCG_GS_GLGeometryContent IS NOT NULL"
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_GLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName + " WHERE "+spatialAttributeName+" IS NOT NULL"
        }
      case CountryLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_CLGeometryContent) AS CL_Geometry FROM "+dataSliceName+ " WHERE DOCG_GS_CLGeometryContent IS NOT NULL"
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_CLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName + " WHERE "+spatialAttributeName+" IS NOT NULL"
        }
      case LocalLevel =>
        if(spatialAttributeName == null){
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS LL_Geometry FROM "+dataSliceName+ " WHERE DOCG_GS_LLGeometryContent IS NOT NULL"
        }else{
          spatialConvertSQL = "SELECT * , ST_GeomFromWKT(DOCG_GS_LLGeometryContent) AS "+spatialAttributeName+" FROM "+dataSliceName + " WHERE "+spatialAttributeName+" IS NOT NULL"
        }
    }
    val orgDataFrame = getDataFrameFromDataSlice(dataSliceName,sliceGroup)
    orgDataFrame.createOrReplaceTempView(dataSliceName)

    val targetDF = getSparkSession().sql(spatialConvertSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }

  def _getJdbcRDD(dataSliceName:String,sliceGroup: String,resultSetConvertor:ResultSetConvertor):RDD[Any]={
    val jdbcURL: String = if (sliceGroup != null) {
      "jdbc:ignite:thin://127.0.0.1/"+sliceGroup+"?partitionAwareness=true"
    }else {
      "jdbc:ignite:thin://127.0.0.1/?partitionAwareness=true"
    }
    val rdd = new JdbcRDD(
      sc,
      () => {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver").newInstance()
        DriverManager.getConnection(jdbcURL)
      },
      "SELECT * FROM "+sliceGroup+"."+dataSliceName,
      1, 100000, 1,
      resultSetConvertor.convertFunction
    )
    rdd
  }

  def getVertexRDD(dataSliceName:String,sliceGroup: String,resultSetConvertor:ResultSetConvertor):RDD[(VertexId, Any)] = {
    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (resultSet.getLong(CoreRealmOperationUtil.RealmGlobalUID),resultSetConvertor.convertFunction(resultSet))
      }
    }
    val jdbcRDD = _getJdbcRDD(dataSliceName,sliceGroup,jdbcResultSetConvertImpl)
    jdbcRDD.asInstanceOf[RDD[(VertexId, Any)]]
  }

  def getVertexRDD(dataSliceName:String,sliceGroup: String):RDD[(VertexId, (String, String))] = {
    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        (resultSet.getLong(CoreRealmOperationUtil.RealmGlobalUID),(dataSliceName,sliceGroup))
      }
    }
    val jdbcRDD = _getJdbcRDD(dataSliceName,sliceGroup,jdbcResultSetConvertImpl)
    jdbcRDD.asInstanceOf[RDD[(VertexId, (String, String))]]
  }

  def getEdgeRDD(dataSliceName:String,sliceGroup: String):RDD[Edge[(Long,String,String)]] = {
    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        Edge(resultSet.getLong(CoreRealmOperationUtil.RelationFromEntityUID),
          resultSet.getLong(CoreRealmOperationUtil.RelationToEntityUID),
          (resultSet.getLong(CoreRealmOperationUtil.RealmGlobalUID),dataSliceName,sliceGroup)
        )
      }
    }
    val jdbcRDD = _getJdbcRDD(dataSliceName,sliceGroup,jdbcResultSetConvertImpl)
    jdbcRDD.asInstanceOf[RDD[Edge[(Long,String,String)]]]
  }

  def getEdgeRDD(dataSliceName:String,sliceGroup: String,resultSetConvertor:ResultSetConvertor):RDD[Edge[(Long,Any)]] = {
    val jdbcResultSetConvertImpl = new ResultSetConvertor {
      override def convertFunction(resultSet: ResultSet): Any = {
        Edge(resultSet.getLong(CoreRealmOperationUtil.RelationFromEntityUID),
          resultSet.getLong(CoreRealmOperationUtil.RelationToEntityUID),
          (resultSet.getLong(CoreRealmOperationUtil.RealmGlobalUID),resultSetConvertor.convertFunction(resultSet))
        )
      }
    }
    val jdbcRDD = _getJdbcRDD(dataSliceName,sliceGroup,jdbcResultSetConvertImpl)
    jdbcRDD.asInstanceOf[RDD[Edge[(Long,Any)]]]
  }

  def getDataSlice(dataSliceName:String): DataSlice = {
    _getDataService().getDataSlice(dataSliceName)
  }

  def close():Unit={
    sparkSession.close()
    _getDataService().close()
  }

  def getSparkSession(): SparkSession = {
    sparkSession
  }

  def _getDataService():DataService = {
    dataService
  }

  def _getDataFrameFromSparkSQL(dataFrameName:String, dataFrameSQL:String):DataFrame = {
    val targetDF = sparkSession.sql(dataFrameSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }
}
