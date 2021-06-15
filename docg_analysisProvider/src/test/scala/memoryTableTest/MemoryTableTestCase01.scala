package memoryTableTest


import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.{MemoryTablePropertyType, MemoryTableServiceInvoker}

import java.util
import java.util.Date
import collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object MemoryTableTestCase01{

  def main(args:Array[String]):Unit ={
    val memoryTableServiceInvoker:MemoryTableServiceInvoker =  MemoryTableServiceInvoker.getInvokerInstance
    val newMemoryTableName = "TestMemoryTable01"+ new Date().getTime
    memoryTableCreate(memoryTableServiceInvoker,newMemoryTableName)
    memoryTableCRUD(memoryTableServiceInvoker,newMemoryTableName)
    memoryTableServiceInvoker.close()
  }

  def memoryTableCreate(memoryTableServiceInvoker:MemoryTableServiceInvoker,memoryTableName:String):Unit={
    println("======== memoryTableCreate Start ===")
    val tablePropertiesDefineMap: java.util.Map[String, MemoryTablePropertyType] = mutable.HashMap(
      "property1" -> MemoryTablePropertyType.STRING,
      "property2" -> MemoryTablePropertyType.INT,
      "property3" -> MemoryTablePropertyType.DOUBLE,
      "property4" -> MemoryTablePropertyType.STRING
    ).asJava

    val pkList: java.util.List[String] = ArrayBuffer("property2").asJava
    val memoryTable = memoryTableServiceInvoker.createGlobalMemoryTable(memoryTableName,"MemoryTableGroup01",tablePropertiesDefineMap,pkList)
    println(memoryTable.getMemoryTableMetaInfo.getMemoryTableName)
    println(memoryTable.getMemoryTableMetaInfo.getBackupDataCount)
    println(memoryTable.getMemoryTableMetaInfo.getTotalDataCount)
    println(memoryTable.getMemoryTableMetaInfo.getStoreBackupNumber)
    println(memoryTable.getMemoryTableMetaInfo.getMemoryTableMode)
    println(memoryTable.getMemoryTableMetaInfo.getMemoryTableGroupName)
    println("======== memoryTableCreate Finish ===")
  }

  def memoryTableCRUD(memoryTableServiceInvoker:MemoryTableServiceInvoker,memoryTableName:String):Unit={
    println("======== memoryTableCRUD Start ===")
    val memoryTable = memoryTableServiceInvoker.getMemoryTable(memoryTableName)
    memoryTable.emptyMemoryTable()
    //C
    val dataRecordsList = new util.ArrayList[util.Map[String, AnyRef]]
    for(i <- 1 to 100000){
      val dataRecordMap: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]
      dataRecordMap.put("property1", "property1-"+i)
      dataRecordMap.put("property2", java.lang.Integer.valueOf(i))
      dataRecordMap.put("property3", java.lang.Double.valueOf(i+1900.22))
      dataRecordMap.put("property4", "property4-".+(i))
      /*
      dataRecordMap.put("property5", java.lang.Float.valueOf(1111))
      dataRecordMap.put("property6", java.lang.Boolean.valueOf(true))
      dataRecordMap.put("property7", java.lang.Short.valueOf("1"))
      dataRecordMap.put("property8", java.lang.Long.valueOf(134000))
      */
      //memoryTable.addDataRecord(dataRecordMap)
      dataRecordsList.add(dataRecordMap)
    }
    /*
    val propertiesNameList = new util.ArrayList[String]
    propertiesNameList.add("property1")
    propertiesNameList.add("property2")
    propertiesNameList.add("property3")
    propertiesNameList.add("property4")
    memoryTable.addDataRecords(propertiesNameList,dataRecordsList)

    println(memoryTable.getMemoryTableMetaInfo.getTotalDataCount)
    //R
    val MemoryTableQueryResult1 = memoryTable.queryDataRecords("SELECT * FROM "+memoryTableName+" where property2 > 100")
    println(MemoryTableQueryResult1.getResultRecords.size())

    val queryParameters = new QueryParameters
    val filteringItem:EqualFilteringItem = new EqualFilteringItem("property2", 50000)
    queryParameters.setDefaultFilteringItem(filteringItem)
    val MemoryTableQueryResult2 = memoryTable.queryDataRecords(queryParameters)
    println(MemoryTableQueryResult2.getResultRecords.size())

    //D
    memoryTable.emptyMemoryTable()
    println(memoryTable.getMemoryTableMetaInfo.getTotalDataCount)

    println("======== memoryTableCRUD Finish ===")

    */
  }
}
