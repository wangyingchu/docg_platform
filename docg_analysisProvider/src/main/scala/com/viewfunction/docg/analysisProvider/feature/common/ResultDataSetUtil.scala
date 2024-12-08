package com.viewfunction.docg.analysisProvider.feature.common

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.{AnalyseRequest, AnalyseResponse}
import com.viewfunction.docg.analysisProvider.fundamental.dataSlice.{DataSliceOperationConstant, DataSliceOperationUtil, ResponseDataSourceTech}
import org.apache.spark.sql.DataFrame

class ResultDataSetUtil {

  def generateResultDataSet(globalDataAccessor:GlobalDataAccessor,
                            dataFrame:DataFrame,
                            analyseResponse:AnalyseResponse):
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
      val dataSliceName:String = analyseResponse.getResponseUUID
      DataSliceOperationUtil.syncDataSliceFromResponseDataset(globalDataAccessor.dataService,dataSliceName,DataSliceOperationConstant.AnalysisResponseDataFormGroup,responseDataset,ResponseDataSourceTech.SPARK)
      responseDataset.clearDataList()
    }

    responseDataset
  }

}
