package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ClassificationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Neo4JClassificationAttachable extends ClassificationAttachable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JClassificationAttachable.class);

    default RelationEntity attachClassification(RelationAttachInfo relationAttachInfo, String classificationName) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                Classification targetClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
                Map<String, Object> relationData = relationAttachInfo.getRelationData();
                RelationDirection relationDirection = relationAttachInfo.getRelationDirection();
                String relationKind = relationAttachInfo.getRelationKind();
                if(targetClassification != null){
                    Neo4JClassificationImpl neo4JClassificationImpl = (Neo4JClassificationImpl)targetClassification;
                    String sourceRelationableUID = null;
                    String targetRelationableUID = null;
                    switch(relationDirection){
                        case FROM:
                            sourceRelationableUID = this.getEntityUID();
                            targetRelationableUID = neo4JClassificationImpl.getClassificationUID();
                            break;
                        case TO:
                            sourceRelationableUID = neo4JClassificationImpl.getClassificationUID();
                            targetRelationableUID = this.getEntityUID();
                            break;
                    }
                    String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(sourceRelationableUID),Long.parseLong(targetRelationableUID), relationKind);

                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (relationKind,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                    Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                    if(existingRelationEntityRes != null){
                        logger.debug("Relation of Kind {} already exist between Entity with UID {} and {}.", relationKind,sourceRelationableUID,targetRelationableUID);
                        return null;
                    }else{
                        Map<String,Object> relationPropertiesMap = relationData != null ? relationData : new HashMap<>();
                        CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                        String createCql = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(sourceRelationableUID),Long.parseLong(targetRelationableUID),
                                relationKind,relationPropertiesMap);
                        Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
                        if(newRelationEntityRes == null){
                            logger.error("Internal error occurs during create relation {} between entity with UID {} and {}.",  relationKind,sourceRelationableUID,targetRelationableUID);
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("Internal error occurs during create relation "+relationKind+" between entity with UID "+sourceRelationableUID+" and "+targetRelationableUID+".");
                            throw exception;
                        }else{
                            return (Neo4JRelationEntityImpl)newRelationEntityRes;
                        }
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default boolean detachClassification(String classificationName, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                Classification targetClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
                if(targetClassification != null){
                    Neo4JClassificationImpl neo4JClassificationImpl = (Neo4JClassificationImpl)targetClassification;
                    String sourceRelationableUID = null;
                    String targetRelationableUID = null;
                    switch(relationDirection){
                        case TWO_WAY:
                            logger.error("TWO_WAY RelationDirection is not allowed in this operation.");
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("TWO_WAY RelationDirection is not allowed in this operation.");
                            throw exception;
                        case FROM:
                            sourceRelationableUID = this.getEntityUID();
                            targetRelationableUID = neo4JClassificationImpl.getClassificationUID();
                            break;
                        case TO:
                            sourceRelationableUID = neo4JClassificationImpl.getClassificationUID();
                            targetRelationableUID = this.getEntityUID();
                            break;
                    }
                    String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(sourceRelationableUID),Long.parseLong(targetRelationableUID), relationKindName);

                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (relationKindName,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                    Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                    if(existingRelationEntityRes == null){
                        logger.debug("Relation of Kind {} does not exist between Entity with UID {} and {}.", relationKindName,sourceRelationableUID,targetRelationableUID);
                        return false;
                    }else{
                        RelationEntity relationEntity = (RelationEntity)existingRelationEntityRes;
                        String deleteCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(
                                CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationEntity.getRelationEntityUID()),null,null);
                        getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                                (relationEntity.getRelationKindName(),workingGraphOperationExecutor);
                        Object deleteRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, deleteCql);
                        if(deleteRelationEntityRes == null){
                            logger.error("Internal error occurs during detach classification {} from entity {}.",  classificationName,this.getEntityUID());
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("Internal error occurs during detach classification "+classificationName+" from entity "+this.getEntityUID()+".");
                            throw exception;
                        }else{
                            return true;
                        }
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default List<Classification> getAttachedClassifications(String relationKindName, RelationDirection relationDirection){
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                RelationDirection realRelationDirection = RelationDirection.TWO_WAY;
                switch(relationDirection){
                    case FROM: realRelationDirection = RelationDirection.TO;break;
                    case TO:realRelationDirection = RelationDirection.FROM;break;
                }
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.getEntityUID()),
                        RealmConstant.ClassificationClass,relationKindName, realRelationDirection,0,0, CypherBuilder.ReturnRelationableDataType.BOTH);
                GetListClassificationTransformer getListClassificationTransformer = new GetListClassificationTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryClassificationRes = workingGraphOperationExecutor.executeRead(getListClassificationTransformer,queryCql);

                if(queryClassificationRes != null){
                    return (List<Classification>)queryClassificationRes;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default List<ClassificationAttachInfo> getAllAttachedClassificationsInfo(){
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                List<ClassificationAttachInfo> classificationAttachInfoList = new ArrayList<>();
                String coreRealmName = null;
                String queryCql ="MATCH (n) -[r]-(classifications:"+RealmConstant.ClassificationClass+") WHERE id(n) = "+this.getEntityUID()+" \n"+
                        "RETURN DISTINCT classifications as classification,r as attachedRelation,properties(r) as relationData";
                logger.debug("Generated Cypher Statement: {}", queryCql);
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord.containsKey("classification") && nodeRecord.containsKey("attachedRelation")){
                                Node classificationNode = nodeRecord.get("classification").asNode();
                                long nodeUID = classificationNode.id();
                                String classificationName = classificationNode.get(RealmConstant._NameProperty).asString();
                                String classificationDesc = null;
                                if(classificationNode.get(RealmConstant._DescProperty) != null){
                                    classificationDesc = classificationNode.get(RealmConstant._DescProperty).asString();
                                }
                                String classificationUID = ""+nodeUID;
                                Neo4JClassificationImpl neo4JClassificationImpl =
                                        new Neo4JClassificationImpl(coreRealmName,classificationName,classificationDesc,classificationUID);
                                neo4JClassificationImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());

                                Relationship attachedRelation = nodeRecord.get("attachedRelation").asRelationship();
                                Map<String,Object> relationDataMap = new HashMap<>();
                                relationDataMap.putAll(nodeRecord.get("relationData").asMap());
                                relationDataMap.remove(RealmConstant._createDateProperty);
                                relationDataMap.remove(RealmConstant._lastModifyDateProperty);
                                relationDataMap.remove(RealmConstant._creatorIdProperty);
                                relationDataMap.remove(RealmConstant._dataOriginProperty);

                                ClassificationAttachInfo currentClassificationAttachInfo = new ClassificationAttachInfo();
                                currentClassificationAttachInfo.setAttachedClassification(neo4JClassificationImpl);

                                RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
                                relationAttachInfo.setRelationKind(attachedRelation.type());
                                relationAttachInfo.setRelationData(relationDataMap);
                                relationAttachInfo.setRelationEntityUID(""+attachedRelation.id());

                                String attachedRelationFromUID = ""+attachedRelation.startNodeId();
                                if(getEntityUID().equals(attachedRelationFromUID)){
                                    relationAttachInfo.setRelationDirection(RelationDirection.FROM);
                                }else{
                                    relationAttachInfo.setRelationDirection(RelationDirection.TO);
                                }
                                currentClassificationAttachInfo.setRelationAttachInfo(relationAttachInfo);

                                classificationAttachInfoList.add(currentClassificationAttachInfo);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
                return classificationAttachInfoList;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default boolean isClassificationAttached(String classificationName,String relationKindName,RelationDirection relationDirection){
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String matchDirectionPart= "-[r:`"+relationKindName+"`]-";
                switch(relationDirection){
                    case FROM -> matchDirectionPart = "-[r:`"+relationKindName+"`]->";
                    case TO -> matchDirectionPart = "<-[r:`"+relationKindName+"`]-";
                    case TWO_WAY -> matchDirectionPart = "-[r:`"+relationKindName+"`]-";
                }
                String queryCql ="MATCH (n) "+matchDirectionPart+"(classification:"+RealmConstant.ClassificationClass+
                        " { "+RealmConstant._NameProperty+":'"+classificationName+"'}) WHERE id(n) = "+this.getEntityUID()+" \n"+
                        "RETURN COUNT(DISTINCT classification) as "+CypherBuilder.operationResultName;
                logger.debug("Generated Cypher Statement: {}", queryCql);

                GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();
                Object queryRes = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,queryCql);
                if(queryRes != null){
                    long matchedClassificationCount = (Long)queryRes;
                    return matchedClassificationCount > 0 ? true:false;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    private Classification getClassificationByName(GraphOperationExecutor workingGraphOperationExecutor,String classificationName) throws CoreRealmServiceRuntimeException{
        String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
        GetSingleClassificationTransformer getSingleClassificationTransformer = new GetSingleClassificationTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
        Object existClassificationRes = workingGraphOperationExecutor.executeRead(getSingleClassificationTransformer,checkCql);
        if(existClassificationRes != null){
            return (Classification)existClassificationRes;
        }else{
            logger.error("Classification with Name {} doesn't exist.", classificationName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Classification with Name "+classificationName+" doesn't exist.");
            throw exception;
        }
    }
}
