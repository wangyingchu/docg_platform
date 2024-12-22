package com.viewfunction.docg.analysisProvider.providerApplication.util

import java.sql.{Connection, DriverManager, SQLException, Statement}

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
}
