package com.viewfunction.docg.analysisProvider.providerApplication

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.viewfunction.docg.analysisProvider.providerApplication.communication.CommunicationActor
import com.viewfunction.docg.analysisProvider.providerApplication.exception.ApplicationInitException
import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.feature.communicationRouter.AnalysisProviderCommunicationMessageHandler
import com.viewfunction.docg.analysisProvider.providerApplication.util.InternalOperationDB

import scala.io.StdIn

object AnalysisProviderApplicationLauncher {

  var applicationRunningFlag = true
  val applicationExitCommand = AnalysisProviderApplicationUtil.getApplicationProperty("applicationExitCommand")
  var globalDataAccessor : GlobalDataAccessor = null
  var engineCommunicationAKKASystem : ActorSystem = null
  var internalOperationDB : InternalOperationDB = null

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
    val providerMaximumDataTransformFrameSize = AnalysisProviderApplicationUtil.getApplicationProperty("providerMaximumDataTransformFrameSize")

    val configStr =
      s"""
         akka {
         |  actor {
         |    # provider=remote is possible, but prefer cluster
         |    provider = cluster
         |    allow-java-serialization = on
         |    serializers {
         |      kryo = "com.twitter.chill.akka.AkkaSerializer"
         |    }
         |    serialization-bindings {
         |      "java.io.Serializable" = none
         |      "com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest" = kryo
         |      "com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse" = kryo
         |    }
         |  }
         |  remote {
         |    artery {
         |      transport = tcp # See Selecting a transport below
         |      canonical.hostname = ""$providerCommunicationHostName""
         |      canonical.port = "$providerCommunicationPort"
         |      advanced{
         |        maximum-frame-size = "$providerMaximumDataTransformFrameSize"
         |      }
         |    }
         |  }
         |  loglevel = WARNING
         |}
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    engineCommunicationAKKASystem = ActorSystem("DOCGAnalysisProviderCommunicationSystem",config)

    internalOperationDB = new InternalOperationDB
    internalOperationDB.startDB()

    val _AnalysisEngineCommunicationMessageHandler = new AnalysisProviderCommunicationMessageHandler(globalDataAccessor)
    val communicationActor = engineCommunicationAKKASystem.actorOf(Props(new CommunicationActor(_AnalysisEngineCommunicationMessageHandler)), name = "communicationRouter")
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
    if(internalOperationDB != null){
      internalOperationDB.shutdownDB()
    }
    true
  }
}
