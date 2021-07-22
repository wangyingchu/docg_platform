package com.viewfunction.docg.analysisProvider.feature.util.coreRealm

import java.sql.ResultSet

class JDBCResultSetConvertor extends Serializable{

  def convertFunction(resultSet:ResultSet):String = {
    resultSet.getString(1)
  }

}
