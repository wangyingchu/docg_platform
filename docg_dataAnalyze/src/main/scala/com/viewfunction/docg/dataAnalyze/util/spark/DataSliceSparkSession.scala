package com.viewfunction.docg.dataAnalyze.util.spark

import org.apache.ignite.Ignition
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.sedona.viz.core.Serde.SedonaVizKryoRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.ignite.IgniteSparkSession

class DataSliceSparkSession {

  private val CONFIG = "configurations/dataCompute-ignite.xml"
  Ignition.setClientMode(true)
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


  def close():Unit={
    // Close IgniteContext on all workers.
    igniteSession.close()
  }
}
