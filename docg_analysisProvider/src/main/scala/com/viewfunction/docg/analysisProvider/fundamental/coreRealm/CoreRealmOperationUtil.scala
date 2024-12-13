package com.viewfunction.docg.analysisProvider.fundamental.coreRealm

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue
import com.viewfunction.docg.coreRealm.realmServiceCore.term.{ConceptionKind, CoreRealm}
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory

import scala.jdk.CollectionConverters._

object CoreRealmOperationUtil {

  val massDataOperationParallelism = AnalysisProviderApplicationUtil.getApplicationProperty("massDataOperationParallelism")

  def syncConceptionKindFromResponseDataset(conceptionKindName:String, responseDataset:ResponseDataset):Unit = {
    val coreRealm:CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      var targetConceptionKind :ConceptionKind = coreRealm.getConceptionKind(conceptionKindName)
      if(targetConceptionKind == null){
        targetConceptionKind = coreRealm.createConceptionKind(conceptionKindName,"AutoCreatedConceptionKind")
      }
      val dataList: java.util.ArrayList[java.util.HashMap[String,Object]]  = responseDataset.getDataList
      val conceptionEntityValueList = new java.util.ArrayList[ConceptionEntityValue]
      dataList.asScala.foreach(mapItem => {
        val currentConceptionEntityValue = new ConceptionEntityValue(mapItem)
        conceptionEntityValueList.add(currentConceptionEntityValue)
      })
      if(dataList.size()> 50000){
        BatchDataOperationUtil.batchAddNewEntities(conceptionKindName,conceptionEntityValueList,massDataOperationParallelism.toInt)
      }else{
        targetConceptionKind.newEntities(conceptionEntityValueList,false)
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
  }
}
