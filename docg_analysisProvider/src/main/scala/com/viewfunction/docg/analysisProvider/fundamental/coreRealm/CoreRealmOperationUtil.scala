package com.viewfunction.docg.analysisProvider.fundamental.coreRealm

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset
import com.viewfunction.docg.analysisProvider.fundamental.coreRealm.ConceptionEntitiesOperationConfig.ConceptionEntitiesInsertMode
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue
import com.viewfunction.docg.coreRealm.realmServiceCore.term.{ConceptionKind, CoreRealm}
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory

import scala.jdk.CollectionConverters._

object CoreRealmOperationUtil {

  val massDataOperationParallelism = AnalysisProviderApplicationUtil.getApplicationProperty("massDataOperationParallelism")

  def syncConceptionKindFromResponseDataset(conceptionKindName:String,
                                            responseDataset:ResponseDataset,
                                            conceptionEntitiesInsertMode:ConceptionEntitiesInsertMode,
                                            conceptionEntityPKAttributeName:String):Unit = {
    val coreRealm:CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      var targetConceptionKind :ConceptionKind = coreRealm.getConceptionKind(conceptionKindName)
      if(targetConceptionKind == null){
        targetConceptionKind = coreRealm.createConceptionKind(conceptionKindName,"AutoCreatedConceptionKind")
      }

      if(conceptionEntitiesInsertMode.equals(ConceptionEntitiesInsertMode.CLEAN_INSERT)){
        targetConceptionKind.purgeAllEntities();
      }

      val dataList: java.util.ArrayList[java.util.HashMap[String,Object]]  = responseDataset.getDataList
      val conceptionEntityValueList = new java.util.ArrayList[ConceptionEntityValue]
      dataList.asScala.foreach(mapItem => {
        val currentConceptionEntityValue = new ConceptionEntityValue(mapItem)
        conceptionEntityValueList.add(currentConceptionEntityValue)
      })
      if(dataList.size()> 50000){
        if(conceptionEntitiesInsertMode.equals(ConceptionEntitiesInsertMode.OVERWRITE)){
          println(conceptionEntityPKAttributeName)
        }else{
          BatchDataOperationUtil.batchAddNewEntities(conceptionKindName,conceptionEntityValueList,massDataOperationParallelism.toInt)
        }
      }else{
        if(conceptionEntitiesInsertMode.equals(ConceptionEntitiesInsertMode.OVERWRITE)){
          println(conceptionEntityPKAttributeName)
        }else{
          targetConceptionKind.newEntities(conceptionEntityValueList,false)
        }
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
  }
}
