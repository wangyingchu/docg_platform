package com.viewfunction.docg.analysisProvider.feature.util.coreRealm

import java.sql.ResultSet

abstract class JDBCResultSetConvertor extends Serializable{
  def convertFunction(resultSet:ResultSet):Any
}
