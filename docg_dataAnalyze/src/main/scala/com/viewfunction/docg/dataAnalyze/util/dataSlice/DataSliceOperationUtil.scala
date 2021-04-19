package com.viewfunction.docg.dataAnalyze.util.dataSlice

object DataSliceOperationUtil {

  def turnOffDataSliceLog(): Unit ={
    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    Logger.getLogger("org.apache.ignite").setLevel(Level.OFF)
  }

  def turnOffSparkLog(): Unit ={
    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
  }












}
