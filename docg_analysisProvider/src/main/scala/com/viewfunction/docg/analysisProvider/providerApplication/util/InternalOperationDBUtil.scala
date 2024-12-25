package com.viewfunction.docg.analysisProvider.providerApplication.util

import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.{FeatureRunningInfo, FunctionalFeatureInfo, ProviderRunningInfo}

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}
import java.time.LocalDateTime
import java.util

object InternalOperationDBUtil {
  val FUNCTIONAL_FEATURE_TABLE_NAME = "FUNCTIONAL_FEATURE"
  val FEATURE_RUNNING_STATUS_NAME = "FEATURE_RUNNING_STATUS"
  val PROVIDER_RUNNING_STATUS_NAME = "PROVIDER_RUNNING_STATUS"

  def checkTableExist(connection : Connection, tableName : String):Boolean = {
    var tableExistFlag = false
    try {
      val stmt = connection.createStatement()
      try {
        val rs = stmt.executeQuery(s"SELECT COUNT(*) FROM SYS.SYSTABLES WHERE TABLENAME = '$tableName'")
        if (rs.next()) {
          val count = rs.getInt(1)
          if (count > 0) {
            tableExistFlag = true
          } else {
            tableExistFlag = false
          }
        }
      } finally {
        stmt.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    tableExistFlag
  }

  def initializeOperationDB(connection : Connection): Unit = {
    val functionalFeatureTableExistFlag = checkTableExist(connection,FUNCTIONAL_FEATURE_TABLE_NAME)
    if(!functionalFeatureTableExistFlag){
      println("init functional feature table")
      val statement = connection.createStatement()
      val createSQL ="CREATE TABLE "+FUNCTIONAL_FEATURE_TABLE_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n feature_name VARCHAR(2048) UNIQUE,\n feature_description VARCHAR(2048)\n)"
      statement.execute(createSQL)
      statement.close()
    }

    val featureRunningStatusTableExistFlag = checkTableExist(connection,FEATURE_RUNNING_STATUS_NAME)
    if(!featureRunningStatusTableExistFlag){
      println("init feature status table")
      val statement = connection.createStatement()
      val createSQL = "CREATE TABLE "+FEATURE_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n feature_running_status VARCHAR(64),\n requestUUID VARCHAR(64),\n request_time TIMESTAMP,\n responseUUID VARCHAR(64),\n feature_name VARCHAR(256),\n response_dataform VARCHAR(64),\n running_startTime TIMESTAMP,\n running_finishTime TIMESTAMP,\n record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
      statement.execute(createSQL)
      statement.close()
    }

    val providerRunningStatusTableExistFlag = checkTableExist(connection,PROVIDER_RUNNING_STATUS_NAME)
    if(!providerRunningStatusTableExistFlag){
      println("init provider status table")
      val statement = connection.createStatement()
      val createSQL ="CREATE TABLE "+PROVIDER_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n provider_startTime TIMESTAMP,\n provider_stopTime TIMESTAMP,\n provider_runningUUID VARCHAR(128)\n,record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
      statement.execute(createSQL)
      statement.close()
    }
  }

  def registerFunctionalFeature(connection:Connection,functionalFeatureName:String,functionalFeatureDesc:String): Boolean = {
    var registerResult:Boolean = false
    try {
      val checkExistenceSQL = "SELECT count(*) FROM "+FUNCTIONAL_FEATURE_TABLE_NAME+" WHERE feature_name = '"+functionalFeatureName+"'"
      val statement = connection.createStatement()
      try {
        val rs = statement.executeQuery(checkExistenceSQL)
        if (rs.next()) {
          val count = rs.getInt(1)
          if (count > 0) {
            registerResult = false
          } else {
            val insertSQL = "INSERT INTO "+FUNCTIONAL_FEATURE_TABLE_NAME+" (feature_name, feature_description)\n VALUES ('"+functionalFeatureName+"', '"+functionalFeatureDesc+"')"
            statement.execute(insertSQL)
            registerResult = true
          }
        }
      } finally {
        statement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    registerResult
  }

  def unregisterFunctionalFeature(connection:Connection,functionalFeatureName:String): Boolean = {
    var unregisterResult:Boolean = false
    try {
      val checkExistenceSQL = "SELECT count(*) FROM "+FUNCTIONAL_FEATURE_TABLE_NAME+" WHERE feature_name = '"+functionalFeatureName+"'"
      val statement = connection.createStatement()
      try {
        val rs = statement.executeQuery(checkExistenceSQL)
        if (rs.next()) {
          val count = rs.getInt(1)
          if (count > 0) {
            val deleteSQL = "DELETE FROM "+FUNCTIONAL_FEATURE_TABLE_NAME+" WHERE feature_name = '"+functionalFeatureName+"'"
            statement.execute(deleteSQL)
            unregisterResult = true
          } else {
            unregisterResult = false
          }
        }
      } finally {
        statement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    unregisterResult
  }

  def recordProviderStart(connection:Connection,runningUUID:String):Unit = {
    try {
      val insertSQL = "INSERT INTO "+PROVIDER_RUNNING_STATUS_NAME+" (provider_startTime, provider_runningUUID)\n VALUES (?,?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(insertSQL)
      try {
        val currentTime = LocalDateTime.now()
        preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(currentTime))
        preparedStatement.setString(2, runningUUID)
        preparedStatement.executeUpdate()
      } finally {
        preparedStatement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  def recordProviderStop(connection:Connection,runningUUID:String):Unit = {
    try {
      val updateSQL = "UPDATE "+PROVIDER_RUNNING_STATUS_NAME+" SET provider_stopTime = ? WHERE provider_runningUUID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(updateSQL)
      try {
        val currentTime = LocalDateTime.now()
        preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(currentTime))
        preparedStatement.setString(2,runningUUID)
        preparedStatement.executeUpdate()
      } finally {
        preparedStatement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  def recordFeatureRequest(connection:Connection,requestUUID:String,requestTime:LocalDateTime,responseUUID:String,responseDataForm:String,runningStartTime:LocalDateTime):Unit = {
    try {
      val insertSQL = "INSERT INTO "+FEATURE_RUNNING_STATUS_NAME+" (feature_running_status,requestUUID,request_time,responseUUID,response_dataform,running_startTime)\n VALUES (?,?,?,?,?,?)"
      val preparedStatement: PreparedStatement = connection.prepareStatement(insertSQL)
      try {
        preparedStatement.setString(1, "ACCEPT")
        preparedStatement.setString(2, requestUUID)
        preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(requestTime))
        preparedStatement.setString(4, responseUUID)
        preparedStatement.setString(5, responseDataForm)
        preparedStatement.setTimestamp(6, java.sql.Timestamp.valueOf(runningStartTime))
        preparedStatement.executeUpdate()
      } finally {
        preparedStatement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  def recordFeatureExecution(connection:Connection,requestUUID:String,functionalFeatureName:String):Unit = {
    try {
      val updateSQL = "UPDATE "+FEATURE_RUNNING_STATUS_NAME+" SET feature_running_status = ?,feature_name = ? WHERE requestUUID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(updateSQL)
      try {
        preparedStatement.setString(1, "EXECUTING")
        preparedStatement.setString(2, functionalFeatureName)
        preparedStatement.setString(3,requestUUID)
        preparedStatement.executeUpdate()
      } finally {
        preparedStatement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  def recordFeatureResponse(connection:Connection,requestUUID:String,runningFinishTime:LocalDateTime):Unit = {
    try {
      val updateSQL = "UPDATE "+FEATURE_RUNNING_STATUS_NAME+" SET feature_running_status = ?,running_finishTime = ? WHERE requestUUID = ?"
      val preparedStatement: PreparedStatement = connection.prepareStatement(updateSQL)
      try {
        preparedStatement.setString(1, "FINISHED")
        preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(runningFinishTime))
        preparedStatement.setString(3,requestUUID)
        preparedStatement.executeUpdate()
      } finally {
        preparedStatement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
  }

  def listFunctionalFeaturesInfo(connection:Connection):util.ArrayList[FunctionalFeatureInfo] = {
    val functionalFeaturesInfoList: util.ArrayList[FunctionalFeatureInfo] = new util.ArrayList[FunctionalFeatureInfo]()
    try {
      val statement = connection.createStatement()
      try{
        val resultSet: ResultSet = statement.executeQuery("SELECT * FROM "+FUNCTIONAL_FEATURE_TABLE_NAME)
        while (resultSet.next()) {
          val column1 = resultSet.getString("feature_name")
          val column2 = resultSet.getString("feature_description")
          val currentFunctionalFeatureInfo = new FunctionalFeatureInfo(column1,column2)
          functionalFeaturesInfoList.add(currentFunctionalFeatureInfo)
        }
      }finally {
        statement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    functionalFeaturesInfoList
  }

  def listFunctionalFeatureRunningStatus(connection:Connection):util.ArrayList[FeatureRunningInfo] = {
    val functionalFeatureRunningStatusInfoList: util.ArrayList[FeatureRunningInfo] = new util.ArrayList[FeatureRunningInfo]()
    try {
      val statement = connection.createStatement()
      try{
        val resultSet: ResultSet = statement.executeQuery("SELECT * FROM "+FEATURE_RUNNING_STATUS_NAME)
        while (resultSet.next()) {
          //  val createSQL = "CREATE TABLE "+FEATURE_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n feature_running_status VARCHAR(64),\n requestUUID VARCHAR(64),\n request_time TIMESTAMP,\n responseUUID VARCHAR(64),\n feature_name VARCHAR(256),\n response_dataform VARCHAR(64),\n running_startTime TIMESTAMP,\n running_finishTime TIMESTAMP,\n record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
          val column1 = resultSet.getString("feature_running_status")
          val column2 = resultSet.getString("requestUUID")
          val column3 = resultSet.getTimestamp("request_time")
          val column4 = resultSet.getString("responseUUID")
          val column5 = resultSet.getString("feature_name")
          val column6 = resultSet.getString("response_dataform")
          val column7 = resultSet.getTimestamp("running_startTime")
          val column8 = resultSet.getTimestamp("running_finishTime")

          //val currentFunctionalFeatureInfo = new FunctionalFeatureInfo(column1,column2)
          //functionalFeaturesInfoList.add(currentFunctionalFeatureInfo)
        }
      }finally {
        statement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    functionalFeatureRunningStatusInfoList
  }

  def listAnalysisProviderRunningStatus(connection:Connection):util.ArrayList[ProviderRunningInfo] = {
    val analysisProviderRunningStatusInfoList: util.ArrayList[ProviderRunningInfo] = new util.ArrayList[ProviderRunningInfo]()
    try {
      val statement = connection.createStatement()
      try{
        val resultSet: ResultSet = statement.executeQuery("SELECT * FROM "+PROVIDER_RUNNING_STATUS_NAME)
        while (resultSet.next()) {
          // val createSQL ="CREATE TABLE "+PROVIDER_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n provider_startTime TIMESTAMP,\n provider_stopTime TIMESTAMP,\n provider_runningUUID VARCHAR(128)\n,record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
          val column1 = resultSet.getTimestamp("provider_startTime")
          val column2 = resultSet.getTimestamp("provider_stopTime")
          val column3 = resultSet.getString("provider_runningUUID")
          //val currentFunctionalFeatureInfo = new FunctionalFeatureInfo(column1,column2)
          //functionalFeaturesInfoList.add(currentFunctionalFeatureInfo)
        }
      }finally {
        statement.close()
      }
    } catch {
      case e: SQLException => e.printStackTrace()
    }
    analysisProviderRunningStatusInfoList
  }
}
