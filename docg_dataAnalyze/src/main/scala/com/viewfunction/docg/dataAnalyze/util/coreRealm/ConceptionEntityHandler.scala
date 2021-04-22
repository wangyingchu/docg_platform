package com.viewfunction.docg.dataAnalyze.util.coreRealm

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity

abstract class ConceptionEntityHandler {
  def handleConceptionEntity(conceptionEntity:ConceptionEntity, entitiesRetrieveStatistics:EntitiesRetrieveStatistics):Any
}
