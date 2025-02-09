package com.viewfunction.docg.analysisProvider.fundamental.relationalDatabase

import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech.ResponseDataSourceTech
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB.{RelationDBOperationUtil, RelationDBPropertyType}

import org.apache.spark.sql.types.DataTypes

import java.util

object RelationalDatabaseOperationUtil {

  def syncDatabaseFromResponseDataset(databaseName:String,
                                      tableName:String,
                                      dataPropertiesDefinitions:java.util.Map[String,String],
                                      dataList: java.util.ArrayList[java.util.Map[String,Object]],
                                      responseDataSourceTech:ResponseDataSourceTech):Unit = {
    val dataTablePropertyMap: util.HashMap[String, RelationDBPropertyType] = new util.HashMap[String, RelationDBPropertyType]()
    dataPropertiesDefinitions.forEach((propertyName,propertyType) =>{
      dataTablePropertyMap.put(propertyName,
        getDataTablePropertyType(propertyType,responseDataSourceTech)
      )
    })
    RelationDBOperationUtil.insertBatchData(databaseName, tableName, dataTablePropertyMap, dataList)
  }

  def getDataTablePropertyType(propertyType:String,responseDataSourceTech:ResponseDataSourceTech):RelationDBPropertyType = {
    var resultType = RelationDBPropertyType.STRING
    if(responseDataSourceTech.equals(ResponseDataSourceTech.SPARK)){
      if(propertyType.equals(DataTypes.StringType.typeName)){resultType = RelationDBPropertyType.STRING}
      if(propertyType.equals(DataTypes.DateType.typeName)){resultType = RelationDBPropertyType.DATE}
      if(propertyType.equals(DataTypes.ShortType.typeName)){resultType = RelationDBPropertyType.SHORT}
      if(propertyType.equals(DataTypes.ByteType.typeName)){resultType = RelationDBPropertyType.BYTE}
      if(propertyType.equals(DataTypes.LongType.typeName)){resultType = RelationDBPropertyType.LONG}
      if(propertyType.equals(DataTypes.BinaryType.typeName)){resultType = RelationDBPropertyType.BYTES}
      if(propertyType.equals(DataTypes.BooleanType.typeName)){resultType = RelationDBPropertyType.BOOLEAN}
      if(propertyType.equals(DataTypes.CalendarIntervalType.typeName)){resultType = RelationDBPropertyType.LONG}
      if(propertyType.equals(DataTypes.DoubleType.typeName)){resultType = RelationDBPropertyType.DOUBLE}
      if(propertyType.equals(DataTypes.FloatType.typeName)){resultType = RelationDBPropertyType.FLOAT}
      if(propertyType.equals(DataTypes.IntegerType.typeName)){resultType = RelationDBPropertyType.INT}
      if(propertyType.equals(DataTypes.TimestampType.typeName)){resultType = RelationDBPropertyType.TIMESTAMP}
      if(propertyType.equals(DataTypes.NullType.typeName)){resultType = RelationDBPropertyType.NULL}
    }
    resultType
  }
}
