package com.viewfunction.docg.analysisProvider.feature.common

import java.sql.ResultSet

abstract class ResultSetConvertor extends Serializable{
  def convertFunction(resultSet:ResultSet):Any
}
