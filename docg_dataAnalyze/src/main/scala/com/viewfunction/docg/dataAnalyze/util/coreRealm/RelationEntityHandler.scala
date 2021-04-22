package com.viewfunction.docg.dataAnalyze.util.coreRealm

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity

abstract class RelationEntityHandler {
  def handleRelationEntity(relationEntity:RelationEntity,entitiesRetrieveStatistics:EntitiesRetrieveStatistics):Any
}
