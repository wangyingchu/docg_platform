package com.viewfunction.docg.dataAnalyze.util.coreRealm

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.{ConceptionEntitiesAttributesRetrieveResult, ConceptionEntityValue, EntitiesRetrieveStatistics, RelationEntitiesAttributesRetrieveResult, RelationEntityValue}
import com.viewfunction.docg.coreRealm.realmServiceCore.term.{ConceptionEntity, ConceptionKind, CoreRealm, RelationEntity, RelationKind}
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory

import collection.JavaConverters._
import collection.mutable.MutableList

class CoreRealmDataAccessor {

  def getConceptionEntities(conceptionKindName: String, queryParameters: QueryParameters, conceptionEntityHandler: ConceptionEntityHandler): List[Any] = {
    val resultList = MutableList[Any]()
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      val conceptionKind :ConceptionKind = coreRealm.getConceptionKind(conceptionKindName)
      if(conceptionKind != null) {
        val resultConceptionEntities = conceptionKind.getEntities(queryParameters)
        val conceptionEntityList: Iterable[ConceptionEntity] = resultConceptionEntities.getConceptionEntities.asScala
        val entitiesRetrieveStatistics:EntitiesRetrieveStatistics = resultConceptionEntities.getOperationStatistics
        conceptionEntityList.foreach(item => {
          val currentResult = conceptionEntityHandler.handleConceptionEntity(item,entitiesRetrieveStatistics)
          resultList += currentResult
        })
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
    resultList.toList
  }
/*
  def getConceptionEntityRowsWithAttributes(conceptionKindName: String, attributeList: List[String], queryParameters: QueryParameters): List[ConceptionEntityValue] = {
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      val conceptionKind :ConceptionKind = coreRealm.getConceptionKind(conceptionKindName)
      if(conceptionKind != null) {
        val resultEntitiesWithAttributes : ConceptionEntitiesAttributesRetrieveResult = conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeList.asJava,queryParameters)
        resultEntitiesWithAttributes.getConceptionEntityValues
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
  }
*/

  def getConceptionEntityByUID(conceptionKindName: String,entityUID:String):Option[ConceptionEntity] = {
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    val conceptionKind :ConceptionKind = coreRealm.getConceptionKind(conceptionKindName)
    if(conceptionKind == null) Option(null)
    else Option(conceptionKind.getEntityByUID(entityUID))
  }

  def getRelationEntities(relationKindName: String, queryParameters: QueryParameters, relationEntityHandler: RelationEntityHandler): List[Any] = {
    val resultList = MutableList[Any]()
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      val relationKind : RelationKind = coreRealm.getRelationKind(relationKindName)
      if(relationKind != null) {
        val resultRelationEntities = relationKind.getRelationEntities(queryParameters)
        val relationEntityList: Iterable[RelationEntity] = resultRelationEntities.getRelationEntities.asScala
        val entitiesRetrieveStatistics:EntitiesRetrieveStatistics = resultRelationEntities.getOperationStatistics
        relationEntityList.foreach(item => {
          val currentResult = relationEntityHandler.handleRelationEntity(item,entitiesRetrieveStatistics)
          resultList += currentResult
        })
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
    resultList.toList
  }
/*
  def getRelationEntityRowsWithAttributes(relationKindName: String, attributeList : List[String], queryParameters: QueryParameters): List[RelationEntityValue] = {
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    try {
      coreRealm.openGlobalSession()
      val relationKind : RelationKind = coreRealm.getRelationKind(relationKindName)
      if(relationKind != null) {
        val relationEntitiesAttributesRetrieveResult : RelationEntitiesAttributesRetrieveResult = relationKind.getEntityAttributesByAttributeNames(attributeList.asJava,queryParameters)
        relationEntitiesAttributesRetrieveResult.getRelationEntityValues
      }
    } finally {
      if (coreRealm != null) {
        coreRealm.closeGlobalSession()
      }
    }
  }
*/

  def getRelationEntityByUID(relationKindName: String,entityUID:String): Option[RelationEntity] = {
    // Wrap the Java result in an Option (this will become a Some or a None)
    val coreRealm :CoreRealm = RealmTermFactory.getDefaultCoreRealm()
    val relationKind :RelationKind = coreRealm.getRelationKind(relationKindName)
    if(relationKind == null) Option(null)
    else Option(relationKind.getEntityByUID(entityUID))
  }

}
