package com.viewfunction.docg.dataAnalyze.util

import com.viewfunction.docg.coreRealm.realmServiceCore.term.{ConceptionEntity, RelationEntity}
import com.viewfunction.docg.dataAnalyze.util.coreRealm.CoreRealmDataAccessor

object CoreRealmDataAccessExample {

  def main(args: Array[String]):Unit = {
    val coreRealmDataAccessor = new CoreRealmDataAccessor
    val resultRelationEntity: Option[RelationEntity] = coreRealmDataAccessor.getRelationEntityByUID("belongsToCategory", "28985571")
    println(resultRelationEntity.get.getFromConceptionEntityUID)

    val resultConceptionEntity: Option[ConceptionEntity] = coreRealmDataAccessor.getConceptionEntityByUID("Ingredient", "14617129")
    println(resultConceptionEntity.get.getAllConceptionKindNames)
  }

}
