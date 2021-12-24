package com.viewfunction.docg.analysisProvider.fundamental.dataSlice

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech.ResponseDataSourceTech
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlice, DataSlicePropertyType}
import org.apache.spark.sql.types.{DataTypes, StructField}

import java.util

object DataSliceOperationUtil {

  def createDataSliceFromResponseDataset(dataServiceInvoker:DataServiceInvoker,dataSliceName:String,dataSliceGroup:String,
                                         responseDataset:ResponseDataset,responseDataSourceTech:ResponseDataSourceTech):Unit = {

    val dataSlicePropertiesDefinitions:java.util.Map[String,String] = responseDataset.getPropertiesInfo

    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertiesDefinitions.forEach((propertyName,propertyType) =>{
      dataSlicePropertyMap.put(propertyName,
        getDataSlicePropertyType(propertyType,responseDataSourceTech)
      )
    })


     val resultDataSlice:DataSlice = dataServiceInvoker.createGridDataSlice(dataSliceName,dataSliceGroup,dataSlicePropertyMap,null)

    //val dataList: java.util.ArrayList[java.util.Map[String,Object]]  = responseDataset.getDataList

    //resultDataSlice.addDataRecords(null,dataList)














  }

  def getDataSlicePropertyType(propertyType:String,responseDataSourceTech:ResponseDataSourceTech):DataSlicePropertyType = {
    //var resultType = DataSlicePropertyType.INT
    var resultType = DataSlicePropertyType.STRING
    if(responseDataSourceTech.equals(ResponseDataSourceTech.SPARK)){
      if(propertyType.equals(DataTypes.StringType.typeName)){resultType = DataSlicePropertyType.STRING}
      if(propertyType.equals(DataTypes.DateType.typeName)){resultType = DataSlicePropertyType.DATE}
      if(propertyType.equals(DataTypes.ShortType.typeName)){resultType = DataSlicePropertyType.SHORT}
      if(propertyType.equals(DataTypes.ByteType.typeName)){resultType = DataSlicePropertyType.BYTE}
      if(propertyType.equals(DataTypes.LongType.typeName)){resultType = DataSlicePropertyType.LONG}
      if(propertyType.equals(DataTypes.BinaryType.typeName)){resultType = DataSlicePropertyType.BINARY}
      if(propertyType.equals(DataTypes.BooleanType.typeName)){resultType = DataSlicePropertyType.BOOLEAN}
      if(propertyType.equals(DataTypes.CalendarIntervalType.typeName)){resultType = DataSlicePropertyType.LONG}
      if(propertyType.equals(DataTypes.DoubleType.typeName)){resultType = DataSlicePropertyType.DOUBLE}
      if(propertyType.equals(DataTypes.FloatType.typeName)){resultType = DataSlicePropertyType.FLOAT}
      if(propertyType.equals(DataTypes.IntegerType.typeName)){resultType = DataSlicePropertyType.INT}
      if(propertyType.equals(DataTypes.TimestampType.typeName)){resultType = DataSlicePropertyType.TIMESTAMP}
      if(propertyType.equals(DataTypes.NullType.typeName)){}
    }
    resultType
  }

}
