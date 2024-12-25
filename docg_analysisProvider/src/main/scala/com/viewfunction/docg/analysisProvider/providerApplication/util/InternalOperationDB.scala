package com.viewfunction.docg.analysisProvider.providerApplication.util

import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FunctionalFeatureInfo

import java.sql.{Connection, DriverManager, SQLException, Statement}
import java.time.LocalDateTime
import java.util

class InternalOperationDB {
  // Derby 数据库 URL
  val dbUrl = "jdbc:derby:internalDB;create=true"
  var connection : Connection = null

  def startDB(): Unit = {
    try {
      // 加载 Derby 驱动
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      // 连接到数据库
      connection = DriverManager.getConnection(dbUrl)
      InternalOperationDBUtil.initializeOperationDB(connection)
      println("OperationDB(Derby) Start success")
      println()
    } catch {
      case e: ClassNotFoundException => e.printStackTrace()
      case e: SQLException =>
        if (e.getSQLState.equals("XJ015")) {
          println("Derby 数据库服务器已成功关闭。")
        } else {
          e.printStackTrace()
        }
    }
  }

  def shutdownDB(): Unit = {
    try {
      if(connection != null){
        // 关闭连接
        connection.close()
        // 停止 Derby 数据库服务器
        DriverManager.getConnection("jdbc:derby:;shutdown=true")
      }
    } catch {
      case e: ClassNotFoundException => e.printStackTrace()
      case e: SQLException =>
        if (e.getSQLState.equals("XJ015")) {
          println("OperationDB(Derby) Shutdown success")
        } else {
          e.printStackTrace()
        }
    }
  }

  def registerFunctionalFeature(functionalFeatureName:String,functionalFeatureDesc:String): Boolean = {
    InternalOperationDBUtil.registerFunctionalFeature(connection,functionalFeatureName,functionalFeatureDesc)
  }

  def unregisterFunctionalFeature(functionalFeatureName:String): Boolean = {
    InternalOperationDBUtil.unregisterFunctionalFeature(connection,functionalFeatureName)
  }

  def recordProviderStart(runningUUID:String):Unit = {
    InternalOperationDBUtil.recordProviderStart(connection,runningUUID)
  }

  def recordProviderStop(runningUUID:String):Unit = {
    InternalOperationDBUtil.recordProviderStop(connection,runningUUID)
  }

  def recordFeatureRequest(requestUUID:String,requestTime:LocalDateTime,responseUUID:String,responseDataForm:String,runningStartTime:LocalDateTime):Unit = {
    InternalOperationDBUtil.recordFeatureRequest(connection,requestUUID,requestTime,responseUUID,responseDataForm,runningStartTime)
  }

  def recordFeatureExecution(requestUUID:String,functionalFeatureName:String):Unit = {
    InternalOperationDBUtil.recordFeatureExecution(connection,requestUUID,functionalFeatureName)
  }

  def recordFeatureResponse(requestUUID:String,runningFinishTime:LocalDateTime):Unit = {
    InternalOperationDBUtil.recordFeatureResponse(connection,requestUUID,runningFinishTime)
  }

  def listFunctionalFeaturesInfo():util.ArrayList[FunctionalFeatureInfo]={
    InternalOperationDBUtil.listFunctionalFeaturesInfo(connection)
  }

  def listFunctionalFeatureRunningStatus():Unit={}

  def listAnalysisProviderRunningStatus():Unit={}
}
