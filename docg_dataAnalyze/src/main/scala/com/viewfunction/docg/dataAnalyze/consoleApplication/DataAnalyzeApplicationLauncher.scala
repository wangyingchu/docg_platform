package com.viewfunction.docg.dataAnalyze.consoleApplication

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.viewfunction.docg.dataAnalyze.consoleApplication.exception.ApplicationInitException
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.transformation.TransformationRouterActor

import scala.io.StdIn

object DataAnalyzeApplicationLauncher {

  var applicationRunningFlag = true
  val applicationExitCommand = ConsoleApplicationUtil.getApplicationInfoPropertyValue("applicationExitCommand")
  var dataSliceSparkAccessor :DataSliceSparkAccessor = null
  var transformationAKKASystem : ActorSystem = null

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

    val config = ConfigFactory.parseString("""
      akka {
        actor {
          provider = "akka.remote.RemoteActorRefProvider"
        }
        serializers {
          kryo = "com.twitter.chill.akka.AkkaSerializer"
        }
        serialization-bindings {
          "java.io.Serializable" = none
          "scala.Product" = kryo
        }

        remote {
          enabled-transports = ["akka.remote.netty.tcp"]
          netty.tcp {
            hostname = 127.0.0.1
            port = 8084
          }
          log-sent-messages = on
          log-received-messages = on
        }
      }
     """)
    transformationAKKASystem = ActorSystem("DataAnalyzeTransformationRouterSystem",config)
    val remoteActor = transformationAKKASystem.actorOf(Props[TransformationRouterActor], name = "TransformationRouter")
    true
  }

  def shutdownApplication():Boolean ={
    if (dataSliceSparkAccessor != null){
      dataSliceSparkAccessor.close()
    }
    if(transformationAKKASystem != null){
      transformationAKKASystem.terminate()
    }
    true
  }
}
