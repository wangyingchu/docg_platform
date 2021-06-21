package com.viewfunction.docg.analysisProvider.providerApplication

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationActor
import com.viewfunction.docg.analysisProvider.providerApplication.exception.ApplicationInitException
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communicationRouter.AnalysisProviderCommunicationMessageHandler

import scala.io.StdIn

object AnalysisProviderApplicationLauncher {

  //https://blog.csdn.net/u013597009/article/details/54972269?utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_v2~rank_aggregation-1-54972269.pc_agg_rank_aggregation&utm_term=%E5%91%BD%E4%BB%A4%E8%A1%8C%E8%BF%90%E8%A1%8Cscala+%E6%96%87%E4%BB%B6&spm=1000.2123.3001.4430

  var applicationRunningFlag = true
  val applicationExitCommand = AnalysisProviderApplicationUtil.getApplicationProperty("applicationExitCommand")
  var globalDataAccessor : GlobalDataAccessor = null
  var engineCommunicationAKKASystem : ActorSystem = null

  def main(args:Array[String]):Unit={

    val initApplicationResult = initApplication()

    if(initApplicationResult){
      AnalysisProviderApplicationUtil.printApplicationBanner()
      while(applicationRunningFlag){
        print("~$ ")
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
    print("Executing - "+ commandContent + " ... ")
    if(applicationExitCommand.equals(commandContent)){
      shutdownApplication()
      applicationRunningFlag = false
    }
    true
  }

  def initApplication():Boolean ={
    val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
    val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
    globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation)

    val providerCommunicationHostName = AnalysisProviderApplicationUtil.getApplicationProperty("providerCommunicationHostName")
    val providerCommunicationPort = AnalysisProviderApplicationUtil.getApplicationProperty("providerCommunicationPort")

    val configStr =
      s"""
         akka {
         |  actor {
         |    # provider=remote is possible, but prefer cluster
         |    provider = cluster
         |    allow-java-serialization = on
         |  }
         |  serializers {
         |    kryo = "com.twitter.chill.akka.AkkaSerializer"
         |  }
         |  serialization-bindings {
         |    "java.io.Serializable" = none
         |    "scala.Product" = kryo
         |  }
         |  remote {
         |    artery {
         |      transport = tcp # See Selecting a transport below
         |      canonical.hostname = ""$providerCommunicationHostName""
         |      canonical.port = "$providerCommunicationPort"
         |    }
         |  }
         |}
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    engineCommunicationAKKASystem = ActorSystem("DOCGAnalysisProviderCommunicationSystem",config)
    val _CIMAnalysisEngineCommunicationMessageHandler = new AnalysisProviderCommunicationMessageHandler(globalDataAccessor)
    val communicationActor = engineCommunicationAKKASystem.actorOf(Props(new CommunicationActor(_CIMAnalysisEngineCommunicationMessageHandler)), name = "communicationRouter")
    //communicationActor ! "DOCG Analysis Provider communication router Started."
    true
  }

  def shutdownApplication():Boolean ={
    if (globalDataAccessor != null){
      globalDataAccessor.close()
    }
    if(engineCommunicationAKKASystem != null){
      engineCommunicationAKKASystem.terminate()
    }
    true
  }
}
