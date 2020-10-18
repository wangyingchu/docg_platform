package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.EntityRelationable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Neo4JEntityRelationable extends EntityRelationable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JEntityRelationable.class);

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
        return false;
    }

    default public List<String> detachAllRelations(){
        return null;
    }

    default public List<String> detachAllSpecifiedRelations(String relationType, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default public List<String> detachSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default Long countRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump) {
        return null;
    }

    default List<ConceptionEntity> getRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump) {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(getEntityUID()),
                        targetConceptionKind,relationKind, relationDirection,1,maxJump);
                GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(targetConceptionKind,workingGraphOperationExecutor);
                Object relationEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
                return relationEntityList != null ? (List<ConceptionEntity>)relationEntityList : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
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
            }else{
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
            }
        }finally {
            getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
        }
        return null;
    }
}
