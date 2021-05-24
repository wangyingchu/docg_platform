package com.viewfunction.docg.dataAnalyze.consoleApplication

import com.viewfunction.docg.dataAnalyze.consoleApplication.exception.ApplicationInitException

import scala.io.StdIn

object DataAnalyzeApplicationLauncher {

  var applicationRunningFlag = true
  val applicationExitCommand = ConsoleApplicationUtil.getApplicationInfoPropertyValue("applicationExitCommand")

  def main(args:Array[String]):Unit={
    ConsoleApplicationUtil.printApplicationConsoleBanner()

    val initApplicationResult = initApplication()

    if(initApplicationResult){
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
    true
  }

  def shutdownApplication():Boolean ={
    true
  }

}
