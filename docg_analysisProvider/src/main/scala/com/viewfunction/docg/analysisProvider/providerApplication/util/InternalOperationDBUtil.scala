package com.viewfunction.docg.analysisProvider.providerApplication.util

import java.sql.{Connection, SQLException}

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
      val createSQL = "CREATE TABLE "+FEATURE_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n event_name VARCHAR(50),\n event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n)"
      statement.execute(createSQL)
      statement.close()
    }

    val providerRunningStatusTableExistFlag = checkTableExist(connection,PROVIDER_RUNNING_STATUS_NAME)
    if(!providerRunningStatusTableExistFlag){
      println("init provider status table")
      val statement = connection.createStatement()
      val createSQL ="CREATE TABLE "+PROVIDER_RUNNING_STATUS_NAME+" (\n id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n feature_name VARCHAR(2048),\n feature_description VARCHAR(2048)\n)"
      statement.execute(createSQL)
      statement.close()
    }
  }

  def registerFunctionalFeature(connection : Connection,functionalFeatureName:String,functionalFeatureDesc:String): Unit = {
    val statement = connection.createStatement()
    val insertSQL = "INSERT INTO "+FUNCTIONAL_FEATURE_TABLE_NAME+" (feature_name, feature_description)\n VALUES ('"+functionalFeatureName+"', '"+functionalFeatureDesc+"');"
    statement.execute(insertSQL)
    statement.close()
  }
}
