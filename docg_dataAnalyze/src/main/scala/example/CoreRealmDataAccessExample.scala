package example

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.{ConceptionEntityValue, EntitiesRetrieveStatistics, RelationEntityValue}
import com.viewfunction.docg.coreRealm.realmServiceCore.term.{ConceptionEntity, RelationEntity}
import com.viewfunction.docg.dataAnalyze.util.coreRealm.{ConceptionEntityHandler, CoreRealmDataAccessor, RelationEntityHandler}

import scala.collection.mutable

object CoreRealmDataAccessExample {

  def main(args: Array[String]):Unit = {
    val coreRealmDataAccessor = new CoreRealmDataAccessor

    val resultRelationEntity: Option[RelationEntity] = coreRealmDataAccessor.getRelationEntityByUID("belongsToCategory", "28985571")
    println(resultRelationEntity.get.getRelationKindName+" - "+resultRelationEntity.get.getRelationEntityUID)

    val resultConceptionEntity: Option[ConceptionEntity] = coreRealmDataAccessor.getConceptionEntityByUID("Ingredient", "14617129")
    println(resultConceptionEntity.get.getAllConceptionKindNames+" - "+resultConceptionEntity.get.getConceptionKindName+" - "+resultConceptionEntity.get.getConceptionEntityUID)

    case class EntityInfo(entityUID: String, entityKind: String)
    val queryParameters = new QueryParameters
    queryParameters.setResultNumber(5)

    val conceptionEntityHandler = new ConceptionEntityHandler(){
      override def handleConceptionEntity(conceptionEntity: ConceptionEntity, entitiesRetrieveStatistics: EntitiesRetrieveStatistics): Any = {
        EntityInfo(conceptionEntity.getConceptionEntityUID,conceptionEntity.getConceptionKindName)
      }
    }
    val conceptionEntityInfoBuffer: mutable.Buffer[EntityInfo] = coreRealmDataAccessor.
      getConceptionEntities("Ingredient",queryParameters,conceptionEntityHandler).asInstanceOf[mutable.Buffer[EntityInfo]]
    conceptionEntityInfoBuffer.foreach(item =>{
      println(item.entityKind + " - " + item.entityUID)
    })

    val relationEntityHandler = new RelationEntityHandler {
      override def handleRelationEntity(relationEntity: RelationEntity, entitiesRetrieveStatistics: EntitiesRetrieveStatistics): Any = {
        EntityInfo(relationEntity.getRelationEntityUID,relationEntity.getRelationKindName)
      }
    }
    val relationEntityInfoBuffer : mutable.Buffer[EntityInfo] = coreRealmDataAccessor.
      getRelationEntities("belongsToCategory",queryParameters,relationEntityHandler).asInstanceOf[mutable.Buffer[EntityInfo]]
    relationEntityInfoBuffer.foreach(item=>{
        println(item.entityKind + " - " + item.entityUID)
      })

    val attributesNameList = mutable.Buffer[String]("category","name")
    val conceptionEntityValueBuffer:mutable.Buffer[ConceptionEntityValue] = coreRealmDataAccessor.getConceptionEntityRowsWithAttributes("Ingredient",attributesNameList,queryParameters)
    conceptionEntityValueBuffer.foreach(item => {
      println(item.getEntityAttributesValue)
      println(item.getConceptionEntityUID)
    })

    val attributesNameList2 = mutable.Buffer[String]("createDate","dataOrigin")
    val relationEntityValueBuffer:mutable.Buffer[RelationEntityValue] = coreRealmDataAccessor.getRelationEntityRowsWithAttributes("isUsedIn",attributesNameList2,queryParameters)
    relationEntityValueBuffer.foreach(item => {
      println(item.getEntityAttributesValue)
      println(item.getRelationEntityUID)
    })

  }

}
