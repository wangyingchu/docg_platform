package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.EntityRelationable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.cypherdsl.core.*;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Neo4JEntityRelationable extends EntityRelationable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JEntityRelationable.class);

    enum NeighborsSearchUsage {Count , Entity}

    default public Long countAllRelations(){
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,getEntityUID(),null,true,null, CypherBuilder.CypherFunctionType.COUNT);
                GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
                Long countResult = (Long)workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
                return countResult;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<RelationEntity> getAllRelations()  {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,getEntityUID(),null,true,null, null);
                GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,queryCql);
                return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<RelationEntity> getAllSpecifiedRelations(String relationKind, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                QueryParameters relationshipQueryParameters = new QueryParameters();
                relationshipQueryParameters.setEntityKind(relationKind);
                relationshipQueryParameters.setResultNumber(10000000);
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,relationshipQueryParameters, null);
                GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,queryCql);
                return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public Long countAllSpecifiedRelations(String relationType, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                QueryParameters relationshipQueryParameters = new QueryParameters();
                relationshipQueryParameters.setEntityKind(relationType);
                relationshipQueryParameters.setResultNumber(10000000);
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,relationshipQueryParameters, CypherBuilder.CypherFunctionType.COUNT);
                GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
                Long countResult = (Long)workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
                return countResult;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public Long countSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection)  throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,exploreParameters,CypherBuilder.CypherFunctionType.COUNT);

                boolean isDistinct = false;
                if(exploreParameters != null){
                    isDistinct = exploreParameters.isDistinctMode();
                }
                GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = isDistinct ?
                        new GetLongFormatAggregatedReturnValueTransformer("count","DISTINCT"):
                        new GetLongFormatAggregatedReturnValueTransformer("count");
                Long countResult = (Long)workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
                return countResult;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<RelationEntity> getSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,exploreParameters,null);
                GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,queryCql);
                return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public RelationEntity attachFromRelation(String targetRelationableUID, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            return attachRelation(getEntityUID(),targetRelationableUID,relationKind,initRelationProperties,repeatable);
        }
        return null;
    }

    default public RelationEntity attachToRelation(String targetRelationableUID, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            return attachRelation(targetRelationableUID,getEntityUID(),relationKind,initRelationProperties,repeatable);
        }
        return null;
    }

    default public boolean detachRelation(String relationEntityUID) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelationWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(relationEntityUID),null,null);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer(null,workingGraphOperationExecutor);
                Object relationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer,queryCql);
                RelationEntity relationEntity = relationEntityRes != null ? (RelationEntity)relationEntityRes : null;
                if(relationEntity != null){
                    if(this.getEntityUID().equals(relationEntity.getFromConceptionEntityUID()) || this.getEntityUID().equals(relationEntity.getToConceptionEntityUID())){
                        String removeCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(relationEntityUID),null,null);
                        relationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,removeCql);
                        relationEntity = relationEntityRes != null ? (RelationEntity)relationEntityRes : null;
                        if(relationEntity != null & relationEntity.getRelationEntityUID().equals(relationEntityUID)){
                            return true;
                        }else{
                            logger.error("Internal error occurs during remove relation Entity with UID {}.",  relationEntityUID);
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("Internal error occurs during remove relation Entity with UID "+relationEntityUID+".");
                            throw exception;
                        }
                    }else{
                        logger.error("RelationEntity with UID {} doesn't related to Entity with UID {}.", relationEntityUID,this.getEntityUID());
                        return false;
                    }
                }else{
                    logger.error("RelationEntity with UID {} doesn't exist.", relationEntityUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("RelationEntity with UID "+relationEntityUID+" doesn't exist.");
                    throw exception;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default public List<String> detachAllRelations(){
        if (this.getEntityUID() != null) {
            String queryCql = null;
            try {
                queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID, getEntityUID(), null, true, null, null);
                return batchDetachRelations(queryCql);
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    default public List<String> detachAllSpecifiedRelations(String relationType, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            try {
                QueryParameters relationshipQueryParameters = new QueryParameters();
                relationshipQueryParameters.setEntityKind(relationType);
                relationshipQueryParameters.setResultNumber(10000000);
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,relationshipQueryParameters, null);
                return batchDetachRelations(queryCql);
                //return null;
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    default public List<String> detachSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        if (this.getEntityUID() != null) {
            try {
                boolean ignoreDirection = true;
                String sourceNodeProperty = null;
                String targetNodeProperty = null;
                if(relationDirection != null){
                    switch (relationDirection){
                        case FROM:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = false;
                            break;
                        case TO:
                            sourceNodeProperty = null;
                            targetNodeProperty = getEntityUID();
                            ignoreDirection = false;
                            break;
                        case TWO_WAY:
                            sourceNodeProperty = getEntityUID();
                            targetNodeProperty = null;
                            ignoreDirection = true;
                            break;
                    }
                }
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,exploreParameters,null);
                return batchDetachRelations(queryCql);
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    default Long countRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump) {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, CypherBuilder.ReturnRelationableDataType.COUNT_NODE);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count","DISTINCT");
                Object countResultResp = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer,queryCql);
                return countResultResp != null ? (Long)countResultResp : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default List<ConceptionEntity> getRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump) {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, CypherBuilder.ReturnRelationableDataType.NODE);
                GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(targetConceptionKind,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
                return relationEntityList != null ? (List<ConceptionEntity>)relationEntityList : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public ConceptionEntitiesAttributesRetrieveResult getAttributesOfRelatedConceptionEntities(String targetConceptionKind, List<String> attributeNames, String relationKind, RelationDirection relationDirection, int maxJump){
        if(attributeNames != null && attributeNames.size()>0 && this.getEntityUID() != null){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, CypherBuilder.ReturnRelationableDataType.NODE);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
                commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
                return commonConceptionEntitiesAttributesRetrieveResultImpl;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default Long countRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump,
                                               AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters,boolean isDistinctMode) throws CoreRealmServiceEntityExploreException {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                ResultEntitiesParameters resultEntitiesParameters = new ResultEntitiesParameters();
                resultEntitiesParameters.setDistinctMode(isDistinctMode);
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters,CypherBuilder.ReturnRelationableDataType.COUNT_NODE);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = isDistinctMode ?
                        new GetLongFormatAggregatedReturnValueTransformer("count","DISTINCT"):
                        new GetLongFormatAggregatedReturnValueTransformer("count");
                Object countResult = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer,queryCql);
                if(countResult != null){
                    return (Long)countResult;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default List<ConceptionEntity> getRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump,
                                                               AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters,CypherBuilder.ReturnRelationableDataType.NODE);
                GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(targetConceptionKind,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
                return relationEntityList != null ? (List<ConceptionEntity>)relationEntityList : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public ConceptionEntitiesAttributesRetrieveResult getAttributesOfRelatedConceptionEntities(String targetConceptionKind, List<String> attributeNames,
                                                                                String relationKind,RelationDirection relationDirection, int maxJump,
                                                                                AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters,
                                                                                                       ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException{
        if(attributeNames != null && attributeNames.size()>0 && this.getEntityUID() != null){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump, relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters,CypherBuilder.ReturnRelationableDataType.NODE);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
                commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
                return commonConceptionEntitiesAttributesRetrieveResultImpl;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<ConceptionEntity> getRelatedConceptionEntities(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch, JumpStopLogic jumpStopLogic, int jumpNumber,
                                                               AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException{
        String cypherProcedureString = generateApocNeighborsQuery(NeighborsSearchUsage.Entity,relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch,
                jumpStopLogic,jumpNumber,conceptionAttributesParameters,resultEntitiesParameters);
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
                return queryResponse != null ? (List<ConceptionEntity>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public Long countRelatedConceptionEntities(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,JumpStopLogic jumpStopLogic,int jumpNumber,
                                               AttributesParameters conceptionAttributesParameters) throws CoreRealmServiceEntityExploreException{
        String cypherProcedureString = generateApocNeighborsQuery(NeighborsSearchUsage.Count,relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch,
                jumpStopLogic,jumpNumber,conceptionAttributesParameters,null);
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer,cypherProcedureString);
                return queryResponse != null ? (Long)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public boolean isDense() {
        if(this.getEntityUID() != null) {
            String cypherProcedureString = "MATCH (targetNode) WHERE id(targetNode)= "+this.getEntityUID()+"\n" +
                    "RETURN apoc.nodes.isDense(targetNode) AS "+ CypherBuilder.operationResultName+";";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetBooleanFormatReturnValueTransformer getBooleanFormatReturnValueTransformer = new GetBooleanFormatReturnValueTransformer();
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getBooleanFormatReturnValueTransformer,cypherProcedureString);
                return queryResponse != null? (Boolean)queryResponse: false;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default public boolean isAttachedTo(String targetRelationableUID,List<RelationKindMatchLogic> relationKindMatchLogics) throws CoreRealmServiceRuntimeException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.nodes/apoc.nodes.connected/
        */
        if(this.getEntityUID() != null && targetRelationableUID != null) {
            String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,null);
            String cypherProcedureString = "MATCH (sourceNode) WHERE id(sourceNode)= "+this.getEntityUID()+"\n" +
                    "MATCH (targetNode) WHERE id(targetNode)= "+targetRelationableUID+"\n" +
                    "RETURN apoc.nodes.connected(sourceNode, targetNode, \""+relationMatchLogicFullString+"\") AS "+CypherBuilder.operationResultName+";";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetBooleanFormatReturnValueTransformer getBooleanFormatReturnValueTransformer = new GetBooleanFormatReturnValueTransformer();
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getBooleanFormatReturnValueTransformer,cypherProcedureString);
                return queryResponse != null? (Boolean)queryResponse: false;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    private String generateApocNeighborsQuery(NeighborsSearchUsage neighborsSearchUsage,List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch, JumpStopLogic jumpStopLogic, int jumpNumber,
                                              AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException {
        if(relationKindMatchLogics != null && relationKindMatchLogics.size() == 0){
            logger.error("At lease one RelationKind must be provided.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("At lease one RelationKind must be provided.");
            throw exception;
        }
        if(relationKindMatchLogics == null & defaultDirectionForNoneRelationKindMatch == null){
            logger.error("At lease one RelationKind or global relation direction must be provided.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("At lease one RelationKind or global relation direction must be provided.");
            throw exception;
        }
        if(relationKindMatchLogics == null & defaultDirectionForNoneRelationKindMatch != null){
            switch(defaultDirectionForNoneRelationKindMatch){
                case TWO_WAY:
                    logger.error("Only FROM or TO direction options are allowed here.");
                    CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
                    exception.setCauseMessage("Only FROM or TO direction options are allowed here.");
                    throw exception;
            }
        }

        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        int distanceNumber = jumpNumber >=1 ? jumpNumber : 1;
        String apocProcedure ="";
        switch(jumpStopLogic){
            case TO:
                apocProcedure = "apoc.neighbors.tohop";
                break;
            case AT:
                apocProcedure = "apoc.neighbors.athop";
        }

        String wherePartQuery = CypherBuilder.generateAttributesParametersQueryLogic(conceptionAttributesParameters,"node");
        String resultPartQuery = generateResultEntitiesParametersFilterLogic(resultEntitiesParameters,"node");
        String returnPartQuery = "";
        switch(neighborsSearchUsage){
            case Entity: returnPartQuery = "RETURN node AS operationResult\n" +resultPartQuery;
                break;
            case Count: returnPartQuery = "RETURN count(node) AS operationResult";
        }

        String wherePartQueryString = wherePartQuery.equals("") ? "":wherePartQuery +"\n";
        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL "+apocProcedure+"(n, \""+relationMatchLogicFullString+"\","+distanceNumber+")\n" +
                "YIELD node\n" +
                wherePartQueryString +
                returnPartQuery;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        return cypherProcedureString;
    }

    private void checkEntityExistence(GraphOperationExecutor workingGraphOperationExecutor,String entityUID) throws CoreRealmServiceRuntimeException{
        String checkCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(entityUID), null, null);
        Object targetEntityExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
        if(!((Boolean)targetEntityExistenceRes).booleanValue()){
            logger.error("Entity with UID {} doesn't exist.", entityUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Entity with UID "+entityUID+" doesn't exist.");
            throw exception;
        }
    }

    private RelationEntity attachRelation(String sourceRelationableUID,String targetRelationableUID, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException{
        GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
        try{
            checkEntityExistence(workingGraphOperationExecutor,sourceRelationableUID);
            checkEntityExistence(workingGraphOperationExecutor,targetRelationableUID);
            if(!repeatable){
                String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(sourceRelationableUID),Long.parseLong(targetRelationableUID), relationKind);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.ConceptionKind_AttributesViewKindRelationClass,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                if(existingRelationEntityRes != null){
                    logger.debug("Relation of Kind {} already exist between Entity with UID {} and {}.", relationKind,sourceRelationableUID,targetRelationableUID);
                    return null;
                }
            }
            Map<String,Object> relationPropertiesMap = initRelationProperties != null ? initRelationProperties : new HashMap<>();
            CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
            String createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(sourceRelationableUID),Long.parseLong(targetRelationableUID),
                    relationKind,relationPropertiesMap);
            GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                    (relationKind,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
            Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
            if(newRelationEntityRes == null){
                logger.error("Internal error occurs during create relation {} between entity with UID {} and {}.",  relationKind,sourceRelationableUID,targetRelationableUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("Internal error occurs during create relation "+relationKind+" between entity with UID "+sourceRelationableUID+" and "+targetRelationableUID+".");
                throw exception;
            }else{
                return (Neo4JRelationEntityImpl)newRelationEntityRes;
            }
        }finally {
            getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
        }
    }

    private List<String> batchDetachRelations(String relationQueryCql){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = relationQueryCql;
                List<Object> relationEntitiesUIDList = new ArrayList<>();
                DataTransformer queryRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    relationEntitiesUIDList.add(relationEntityUID);
                                }
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(queryRelationshipOperationDataTransformer, queryCql);
                String detachCql = CypherBuilder.deleteRelationsWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, relationEntitiesUIDList);
                DataTransformer detachRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        List<String> resultEntitiesUIDList = new ArrayList<>();
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    resultEntitiesUIDList.add("" + relationEntityUID);
                                }
                            }
                        }
                        return resultEntitiesUIDList;
                    }
                };
                Object detachResult = workingGraphOperationExecutor.executeWrite(detachRelationshipOperationDataTransformer, detachCql);
                if (detachResult != null) {
                    return (List<String>) detachResult;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private String generateResultEntitiesParametersFilterLogic(ResultEntitiesParameters resultEntitiesParameters,String filterNodeName) throws CoreRealmServiceEntityExploreException {
        if(resultEntitiesParameters != null){
            Node resultNodes = Cypher.anyNode().named(filterNodeName);

            int startPage = resultEntitiesParameters.getStartPage();
            int endPage = resultEntitiesParameters.getEndPage();
            int pageSize = resultEntitiesParameters.getPageSize();
            int resultNumber = resultEntitiesParameters.getResultNumber();
            int defaultReturnRecordNumber = 10000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;
            List<SortingItem> sortingItemList = resultEntitiesParameters.getSortingItems();
            SortItem[] sortItemArray = null;

            if (sortingItemList!= null && (sortingItemList.size() > 0)){
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).ascending();
                            break;
                        case DESC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).descending();
                    }
                }
            }

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }

                int runtimePageSize = pageSize != 0 ? pageSize : 50;
                int runtimeStartPage = startPage - 1;

                if (endPage != 0) {
                    //get data from start page to end page, each page has runtimePageSize number of record
                    if (endPage < 0 || endPage <= startPage) {
                        String exceptionMessage = "end page must great than start page";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    int runtimeEndPage = endPage - 1;

                    skipRecordNumber = runtimePageSize * runtimeStartPage;
                    limitRecordNumber = (runtimeEndPage - runtimeStartPage) * runtimePageSize;
                } else {
                    //filter the data before the start page
                    limitRecordNumber = runtimePageSize * runtimeStartPage;
                }
            } else {
                //if there is no page parameters,use resultNumber to control result information number
                if (resultNumber != 0) {
                    if (resultNumber < 0) {
                        String exceptionMessage = "result number must great then zero";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    limitRecordNumber = resultNumber;
                }
            }
            if (limitRecordNumber == 0) {
                limitRecordNumber = defaultReturnRecordNumber;
            }

            StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(resultNodes);
            StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
            ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(resultNodes);
            Statement statement;

            if (skipRecordNumber != 0){
                if(sortItemArray != null){
                    statement = ongoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                }else{
                    statement = ongoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            }else{
                if(sortItemArray != null){
                    statement = ongoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                }else{
                    statement = ongoingReadingAndReturn.limit(limitRecordNumber).build();
                }
            }

            Renderer cypherRenderer = Renderer.getDefaultRenderer();
            String rel = cypherRenderer.render(statement);

            String tempStringToReplace = "MATCH ("+filterNodeName+") RETURN "+filterNodeName+" ";
            return rel.replace(tempStringToReplace,"");
        }
        return "";
    }
}
