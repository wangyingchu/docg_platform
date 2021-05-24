package com.viewfunction.docg.dataAnalyze.consoleApplication

import com.viewfunction.docg.dataAnalyze.consoleApplication.exception.ApplicationInitException
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor

import scala.io.StdIn

object DataAnalyzeApplicationLauncher {

  var applicationRunningFlag = true
  val applicationExitCommand = ConsoleApplicationUtil.getApplicationInfoPropertyValue("applicationExitCommand")
  var dataSliceSparkAccessor :DataSliceSparkAccessor = null

  def main(args:Array[String]):Unit={

    val initApplicationResult = initApplication()

    if(initApplicationResult){
      ConsoleApplicationUtil.printApplicationConsoleBanner()
      while(applicationRunningFlag){
        print(">_ ")
        val name:String = StdIn.readLine()
        val executeResult = executeInputCommand(name)
        if(executeResult){
          println("")
        }
      }
    }else{
      throw new ApplicationInitException
    }
  }

  def executeInputCommand(commandContent:String):Boolean ={
    print("Processing ["+ commandContent + "] ... ")
    if(applicationExitCommand.equals(commandContent)){
      shutdownApplication()
      applicationRunningFlag = false
    }
    true
  }

  def initApplication():Boolean ={
    val sparkApplicationName = ConsoleApplicationUtil.getApplicationInfoPropertyValue("sparkApplicationName")
    val sparkMasterLocation = ConsoleApplicationUtil.getApplicationInfoPropertyValue("sparkMasterLocation")
    val sparkExecutorInstanceNumber = ConsoleApplicationUtil.getApplicationInfoPropertyValue("sparkExecutorInstanceNumber")
    dataSliceSparkAccessor = new DataSliceSparkAccessor(sparkApplicationName,sparkMasterLocation,sparkExecutorInstanceNumber)
    true
  }

  def shutdownApplication():Boolean ={
    if (dataSliceSparkAccessor != null){
      dataSliceSparkAccessor.close()
    }
    true
  }
}
