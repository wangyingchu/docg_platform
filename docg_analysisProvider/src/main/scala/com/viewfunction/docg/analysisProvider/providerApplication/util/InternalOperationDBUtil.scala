package com.viewfunction.docg.analysisProvider.providerApplication.util

import java.sql.{Connection, PreparedStatement, SQLException}
import java.time.LocalDateTime

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
      val createSQL = "CREATE TABLE "+FEATURE_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n event_name VARCHAR(50),\n record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
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
}
