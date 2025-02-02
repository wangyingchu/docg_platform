package com.viewfunction.docg.analysisProvider.feature.common

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse, ResponseDataset}
import com.viewfunction.docg.analysisProvider.fundamental.coreRealm.ConceptionEntitiesOperationConfig.ConceptionEntitiesInsertMode
import com.viewfunction.docg.analysisProvider.fundamental.coreRealm.{CoreRealmOperationClientConstant, CoreRealmOperationUtil}
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.ResponseDataSourceTech.ResponseDataSourceTech
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.{DataSliceOperationClientConstant, DataSliceOperationConstant, DataSliceOperationUtil, ResponseDataSourceTech}
import com.viewfunction.docg.analysisProvider.fundamental.messageQueue.{MessageQueueOperationClientConstant, MessageQueueOperationUtil}
import com.viewfunction.docg.analysisProvider.fundamental.relationalDatabase.{RelationalDatabaseOperationConstant, RelationalDatabaseOperationUtil}
import org.apache.spark.sql.DataFrame

import java.util

class ResultDataSetUtil {

  def generateResultDataList(dataFrame:DataFrame,resultDataList:java.util.ArrayList[java.util.HashMap[String,Object]]):Unit= {
    println(" Start execute generateResultDataList ...")

    val structureFields =dataFrame.schema.fields
    val propertiesMetaInfo = new java.util.HashMap[String,Object]
    structureFields.foreach(item =>{
      propertiesMetaInfo.put(item.name,item.dataType.typeName)
    })

    val dataRowArray = dataFrame.collect()
    println("   "+dataRowArray.length+" rows data collected")
    dataRowArray.foreach(row=>{
      val currentMap = new java.util.HashMap[String,Object]
      resultDataList.add(currentMap)
      structureFields.foreach(fieldStructure=>{
        currentMap.put(fieldStructure.name,row.get(row.fieldIndex(fieldStructure.name)).asInstanceOf[AnyRef])
      })
    })
  }

  def generateResultDataSet(globalDataAccessor:GlobalDataAccessor,
                            propertiesInfoList:java.util.ArrayList[java.util.HashMap[String,Object]],
                            resultDataList:java.util.ArrayList[java.util.HashMap[String,Object]],
                            analyseResponse:AnalyseResponse,
                            analyseRequest:AnalyseRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println(" Start execute generateResultDataSet ...")

    val responseDataset = new com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset(propertiesInfoList,resultDataList)
    analyseResponse.setResponseData(responseDataset)

    val responseDataFormValue = analyseResponse.getResponseDataForm
    if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.STREAM_BACK)){
      //need do nothing
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DATA_SLICE)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.CONCEPTION_KIND)){
      sendToConceptionKind(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.MESSAGE_QUEUE)){
      sendToMessageQueue(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.R_DATABASE)){
      sendToRelationalDB(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_RDB)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToRelationalDB(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_MQ)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToMessageQueue(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_CK)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToConceptionKind(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }
    responseDataset
  }

  def generateResultDataSet(globalDataAccessor:GlobalDataAccessor,
                            dataFrame:DataFrame,
                            analyseResponse:AnalyseResponse,
                            analyseRequest:AnalyseRequest):
  com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset = {
    println(" Start execute generateResultDataSet ...")

    val structureFields =dataFrame.schema.fields
    val propertiesMetaInfo = new java.util.HashMap[String,Object]
    structureFields.foreach(item =>{
      propertiesMetaInfo.put(item.name,item.dataType.typeName)
    })
    val propertiesInfoList = new java.util.ArrayList[java.util.HashMap[String,Object]]
    propertiesInfoList.add(propertiesMetaInfo)

    val responseDataList = new java.util.ArrayList[java.util.HashMap[String,Object]]
    val responseDataset = new com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset(propertiesInfoList,responseDataList)
    analyseResponse.setResponseData(responseDataset)

    val dataRowArray = dataFrame.collect()
    dataRowArray.foreach(row=>{
      val currentMap = new java.util.HashMap[String,Object]
      responseDataList.add(currentMap)
      structureFields.foreach(fieldStructure=>{
        currentMap.put(fieldStructure.name,row.get(row.fieldIndex(fieldStructure.name)).asInstanceOf[AnyRef])
      })
    })

    val responseDataFormValue = analyseResponse.getResponseDataForm
    if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.STREAM_BACK)){
      //need do nothing
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DATA_SLICE)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.CONCEPTION_KIND)){
      sendToConceptionKind(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.MESSAGE_QUEUE)){
      sendToMessageQueue(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.R_DATABASE)){
      sendToRelationalDB(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_RDB)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToRelationalDB(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_MQ)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToMessageQueue(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }else if(responseDataFormValue.equals(AnalyseRequest.ResponseDataForm.DS_and_CK)){
      sendToDataSlice(globalDataAccessor,analyseRequest,analyseResponse,responseDataset,ResponseDataSourceTech.SPARK)
      sendToConceptionKind(analyseRequest,responseDataset)
      responseDataset.clearDataList()
    }
    responseDataset
  }

  /* utility functions */
  private def sendToMessageQueue(analyseRequest:AnalyseRequest,
                                 responseDataset:ResponseDataset):Unit = {
    if(analyseRequest.getRequestParameters != null){
      val requestParameters:util.HashMap[String,AnyRef] = analyseRequest.getRequestParameters.asInstanceOf[util.HashMap[String,AnyRef]]
      var topicName:String = null
      var topicTag:String = null
      var topicProperties:java.util.HashMap[String,String] = null
      if(requestParameters.containsKey(MessageQueueOperationClientConstant.MessageTopic)){
        topicName = requestParameters.get(MessageQueueOperationClientConstant.MessageTopic).toString
      }
      if(requestParameters.containsKey(MessageQueueOperationClientConstant.MessageTag)){
        topicTag = requestParameters.get(MessageQueueOperationClientConstant.MessageTag).toString
      }
      if(requestParameters.containsKey(MessageQueueOperationClientConstant.MessageProperties)){
        topicProperties = requestParameters.get(MessageQueueOperationClientConstant.MessageProperties).asInstanceOf[java.util.HashMap[String,String]]
      }
      if(topicName != null){
        MessageQueueOperationUtil.sendMessageFromResponseDataset(topicName,topicTag,topicProperties,responseDataset)
      }
    }
  }

  private def sendToDataSlice(globalDataAccessor:GlobalDataAccessor,
                      analyseRequest:AnalyseRequest,
                      analyseResponse:AnalyseResponse,
                      responseDataset:ResponseDataset,
                      responseDataSourceTech:ResponseDataSourceTech):Unit = {
    var dataSliceName:String = analyseResponse.getResponseUUID
    var createNewSlice = true
    if(analyseRequest.getRequestParameters != null){
      val requestParameters:util.HashMap[String,AnyRef] = analyseRequest.getRequestParameters.asInstanceOf[util.HashMap[String,AnyRef]]
      if(requestParameters.containsKey(DataSliceOperationClientConstant.DataSliceName)){
        dataSliceName = requestParameters.get(DataSliceOperationClientConstant.DataSliceName).toString
        createNewSlice = false
      }
    }
    DataSliceOperationUtil.syncDataSliceFromResponseDataset(globalDataAccessor.dataService,dataSliceName,DataSliceOperationConstant.AnalysisResponseDataFormGroup,responseDataset, responseDataSourceTech,createNewSlice)
  }

  private def sendToConceptionKind(analyseRequest:AnalyseRequest,responseDataset:ResponseDataset):Unit = {
    if(analyseRequest.getRequestParameters != null){
      val requestParameters:util.HashMap[String,AnyRef] = analyseRequest.getRequestParameters.asInstanceOf[util.HashMap[String,AnyRef]]
      if(requestParameters.containsKey(CoreRealmOperationClientConstant.ConceptionKindName)){
        val targetConceptionKind:String = requestParameters.get(CoreRealmOperationClientConstant.ConceptionKindName).toString

        var conceptionEntitiesInsertMode = ConceptionEntitiesInsertMode.APPEND
        if(requestParameters.containsKey(CoreRealmOperationClientConstant.ConceptionEntitiesInsertMode)){
          val requestInsertMode:String = requestParameters.get(CoreRealmOperationClientConstant.ConceptionEntitiesInsertMode).toString
          if(ConceptionEntitiesInsertMode.CLEAN_INSERT.toString.equals(requestInsertMode)){
            conceptionEntitiesInsertMode = ConceptionEntitiesInsertMode.CLEAN_INSERT
          }else if(ConceptionEntitiesInsertMode.OVERWRITE.toString.equals(requestInsertMode)){
            conceptionEntitiesInsertMode = ConceptionEntitiesInsertMode.OVERWRITE
          }else if(ConceptionEntitiesInsertMode.APPEND.toString.equals(requestInsertMode)){
            conceptionEntitiesInsertMode = ConceptionEntitiesInsertMode.APPEND
          }else{}
        }

        var conceptionEntityPKAttributeName:String = null
        if(requestParameters.containsKey(CoreRealmOperationClientConstant.ConceptionEntityPKAttributeName)){
          conceptionEntityPKAttributeName = requestParameters.get(CoreRealmOperationClientConstant.ConceptionEntityPKAttributeName).toString
        }

        CoreRealmOperationUtil.syncConceptionKindFromResponseDataset(targetConceptionKind,responseDataset,conceptionEntitiesInsertMode,conceptionEntityPKAttributeName)
      }
    }
  }
  private def sendToRelationalDB(analyseRequest:AnalyseRequest,
                                 responseDataset:ResponseDataset):Unit = {
    if(analyseRequest.getRequestParameters != null){
      val requestParameters:util.HashMap[String,AnyRef] = analyseRequest.getRequestParameters.asInstanceOf[util.HashMap[String,AnyRef]]
      var databaseName:String = null
      var tableName:String = null
      if(requestParameters.containsKey(RelationalDatabaseOperationConstant.DatabaseName)){
        databaseName = requestParameters.get(RelationalDatabaseOperationConstant.DatabaseName).toString
      }
      if(requestParameters.containsKey(RelationalDatabaseOperationConstant.TableName)){
        tableName = requestParameters.get(RelationalDatabaseOperationConstant.TableName).toString
      }
      if(databaseName != null && tableName != null){
        val dataTablePropertiesDefinitions:java.util.Map[String,String] = responseDataset.getPropertiesInfo
        val dataList: java.util.ArrayList[java.util.Map[String,Object]] = responseDataset.getDataList.asInstanceOf[java.util.ArrayList[java.util.Map[String,Object]]]
        RelationalDatabaseOperationUtil.syncDatabaseFromResponseDataset(databaseName,tableName,dataTablePropertiesDefinitions,dataList,ResponseDataSourceTech.SPARK)
      }
    }
  }

}
