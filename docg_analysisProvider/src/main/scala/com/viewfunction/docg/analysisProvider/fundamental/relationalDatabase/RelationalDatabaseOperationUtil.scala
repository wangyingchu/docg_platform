package com.viewfunction.docg.analysisProvider.fundamental.relationalDatabase

import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech.ResponseDataSourceTech
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.rationalDB.{RationalDBOperationUtil, RationalDBPropertyType}

import org.apache.spark.sql.types.DataTypes

import java.util

object RelationalDatabaseOperationUtil {

  def syncDatabaseFromResponseDataset(databaseName:String,
                                      tableName:String,
                                      dataPropertiesDefinitions:java.util.Map[String,String],
                                      dataList: java.util.ArrayList[java.util.Map[String,Object]],
                                      responseDataSourceTech:ResponseDataSourceTech):Unit = {
    val dataTablePropertyMap: util.HashMap[String, RationalDBPropertyType] = new util.HashMap[String, RationalDBPropertyType]()
    dataPropertiesDefinitions.forEach((propertyName,propertyType) =>{
      dataTablePropertyMap.put(propertyName,
        getDataTablePropertyType(propertyType,responseDataSourceTech)
      )
    })
    RationalDBOperationUtil.insertBatchData(databaseName, tableName, dataTablePropertyMap, dataList)
  }

  def getDataTablePropertyType(propertyType:String,responseDataSourceTech:ResponseDataSourceTech):RationalDBPropertyType = {
    var resultType = RationalDBPropertyType.STRING
    if(responseDataSourceTech.equals(ResponseDataSourceTech.SPARK)){
      if(propertyType.equals(DataTypes.StringType.typeName)){resultType = RationalDBPropertyType.STRING}
      if(propertyType.equals(DataTypes.DateType.typeName)){resultType = RationalDBPropertyType.DATE}
      if(propertyType.equals(DataTypes.ShortType.typeName)){resultType = RationalDBPropertyType.SHORT}
      if(propertyType.equals(DataTypes.ByteType.typeName)){resultType = RationalDBPropertyType.BYTE}
      if(propertyType.equals(DataTypes.LongType.typeName)){resultType = RationalDBPropertyType.LONG}
      if(propertyType.equals(DataTypes.BinaryType.typeName)){resultType = RationalDBPropertyType.BYTES}
      if(propertyType.equals(DataTypes.BooleanType.typeName)){resultType = RationalDBPropertyType.BOOLEAN}
      if(propertyType.equals(DataTypes.CalendarIntervalType.typeName)){resultType = RationalDBPropertyType.LONG}
      if(propertyType.equals(DataTypes.DoubleType.typeName)){resultType = RationalDBPropertyType.DOUBLE}
      if(propertyType.equals(DataTypes.FloatType.typeName)){resultType = RationalDBPropertyType.FLOAT}
      if(propertyType.equals(DataTypes.IntegerType.typeName)){resultType = RationalDBPropertyType.INT}
      if(propertyType.equals(DataTypes.TimestampType.typeName)){resultType = RationalDBPropertyType.TIMESTAMP}
      if(propertyType.equals(DataTypes.NullType.typeName)){resultType = RationalDBPropertyType.NULL}
    }
    resultType
  }
}
