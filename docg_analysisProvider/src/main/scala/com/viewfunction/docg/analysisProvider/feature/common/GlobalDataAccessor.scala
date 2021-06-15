package com.viewfunction.docg.analysisProvider.feature.common

import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.query.QueryParameters
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.result.MemoryTableQueryResult
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.{MemoryTable, MemoryTableServiceInvoker}
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import org.apache.ignite.{Ignite, Ignition}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.lang.Boolean

class GlobalDataAccessor (private val sessionName:String, private val masterLocation:String, private val executorInstanceNumber:String){

  val isClientIgniteNode = Boolean.parseBoolean(AnalysisProviderApplicationUtil.getApplicationProperty("isClientIgniteNode"))
  Ignition.setClientMode(isClientIgniteNode)
  val igniteNode = Ignition.start("configurations/dataAnalysis-ignite.xml")
  val memoryTableServiceInvoker = MemoryTableServiceInvoker.getInvokerInstance(igniteNode)

  val sparkSession : SparkSession = SparkSession.builder.appName(sessionName).master(masterLocation)
    .config("spark.executor.instances", executorInstanceNumber)
    .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
    .config("spark.kryo.registrator", classOf[SedonaVizKryoRegistrator].getName) // org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
    .getOrCreate()
  //Register Sedona SQL functions
  SedonaSQLRegistrator.registerAll(sparkSession)

  def getDataFrameFromMemoryTable(memoryTableName: String, memoryTableGroup: String):DataFrame={
    val jdbcURL: String = if (memoryTableGroup != null) {
      "jdbc:ignite:thin://127.0.0.1/"+memoryTableGroup+"?partitionAwareness=true"
    }else {
      "jdbc:ignite:thin://127.0.0.1/?partitionAwareness=true"
    }
     val df = _getSparkSession().sqlContext.read.format("jdbc")
      .option("url", jdbcURL)
      .option("driver", "org.apache.ignite.IgniteJdbcThinDriver")
      .option("dbtable", memoryTableName)
      .option("fetchSize",10000)
      .load()
    df
  }

  def getMemoryTable(tableName:String):MemoryTable={
    _getMemoryTableServiceInvoker().getMemoryTable(tableName)
  }

  def close():Unit={
    sparkSession.close()
    igniteNode.close()
  }

  def _getSparkSession():SparkSession = {
    sparkSession
  }

  def _getIgniteNode(): Ignite = {
    igniteNode
  }

  def _getMemoryTableServiceInvoker():MemoryTableServiceInvoker = {
    memoryTableServiceInvoker
  }

  def _getDataFrameFromSparkSQL(dataFrameName:String, dataFrameSQL:String):DataFrame = {
    val targetDF = sparkSession.sql(dataFrameSQL.stripMargin)
    if(null != dataFrameName){
      targetDF.createOrReplaceTempView(dataFrameName)
    }
    targetDF
  }
}
