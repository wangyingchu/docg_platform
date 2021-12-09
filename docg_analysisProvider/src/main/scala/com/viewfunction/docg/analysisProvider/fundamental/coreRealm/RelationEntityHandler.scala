package com.viewfunction.docg.analysisProvider.fundamental.coreRealm

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity

abstract class RelationEntityHandler {
  def handleRelationEntity(relationEntity:RelationEntity,entitiesRetrieveStatistics:EntitiesRetrieveStatistics):Any
}
