package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationEntityValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLongFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonRelationEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonRelationEntitiesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationKind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JRelationKindImpl implements Neo4JRelationKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JRelationKindImpl.class);
    private String coreRealmName;
    private String relationKindName;
    private String relationKindDesc;
    private String relationKindUID;

    public Neo4JRelationKindImpl(String coreRealmName, String relationKindName, String relationKindDesc, String relationKindUID){
        this.coreRealmName = coreRealmName;
        this.relationKindName = relationKindName;
        this.relationKindDesc = relationKindDesc;
        this.relationKindUID = relationKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getRelationKindUID(){
        return this.relationKindUID;
    }

    @Override
    public String getRelationKindName() {
        return this.relationKindName;
    }

    @Override
    public String getRelationKindDesc() {
        return this.relationKindDesc;
    }

    @Override
    public RelationKind getParentRelationKind() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public List<RelationKind> getChildRelationKinds() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public InheritanceTree<RelationKind> getOffspringRelationKinds() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public Long countRelationEntities() throws CoreRealmServiceRuntimeException{
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String queryCql = CypherBuilder.matchRelationWithSinglePropertyValueAndFunction(getRelationKindName(), CypherBuilder.CypherFunctionType.COUNT, null, null);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countRelationEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countRelationEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countRelationEntitiesRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Long countRelationEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public Long countRelationEntities(AttributesParameters attributesParameters,boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if (attributesParameters != null) {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                queryParameters.setEntityKind(this.relationKindName);
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,
                        null,null,true,queryParameters, CypherBuilder.CypherFunctionType.COUNT);

                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer =
                        queryParameters.isDistinctMode() ?
                        new GetLongFormatAggregatedReturnValueTransformer("count","DISTINCT"):
                                new GetLongFormatAggregatedReturnValueTransformer("count");
                Object queryRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer,queryCql);
                if (queryRes != null) {
                    return (Long) queryRes;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return null;
        }else{
            return countRelationEntities();
        }
    }

    @Override
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if (queryParameters != null) {
            CommonRelationEntitiesRetrieveResultImpl commonRelationEntitiesRetrieveResultImpl = new CommonRelationEntitiesRetrieveResultImpl();
            commonRelationEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                queryParameters.setEntityKind(this.relationKindName);
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,
                        null,null,true,queryParameters,null);
                GetListRelationEntityTransformer getListRelationEntityTransformer =
                        new GetListRelationEntityTransformer(this.relationKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,queryCql);
                if(queryRes != null){
                    List<RelationEntity> resultConceptionEntityList = (List<RelationEntity>)queryRes;
                    commonRelationEntitiesRetrieveResultImpl.addRelationEntities(resultConceptionEntityList);
                    commonRelationEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonRelationEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonRelationEntitiesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public EntitiesOperationResult purgeAllRelationEntities() throws CoreRealmServiceRuntimeException {
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String deleteCql = CypherBuilder.deleteRelationTypeWithSinglePropertyValueAndFunction(this.relationKindName,
                    CypherBuilder.CypherFunctionType.COUNT,null,null);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer =
                    new GetLongFormatAggregatedReturnValueTransformer("count","DISTINCT");
            Object deleteResultObject = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
            if(deleteResultObject == null){
                throw new CoreRealmServiceRuntimeException();
            }else{
                commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount((Long)deleteResultObject);
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("purgeAllRelationEntities operation for relationKind "+this.relationKindName+" success.");
            }
            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public RelationEntitiesAttributesRetrieveResult getEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if(attributeNames != null && attributeNames.size()>0){
            CommonRelationEntitiesAttributesRetrieveResultImpl commonRelationEntitiesAttributesRetrieveResultImpl =
                    new CommonRelationEntitiesAttributesRetrieveResultImpl();
            commonRelationEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            if(queryParameters == null){
                queryParameters = new QueryParameters();
            }

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                queryParameters.setEntityKind(this.relationKindName);
                queryParameters.setDistinctMode(true);
                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,
                        null,null,true,queryParameters,null);
                GetListRelationEntityValueTransformer getListRelationEntityValueTransformer =
                        new GetListRelationEntityValueTransformer(this.relationKindName,attributeNames);
                Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationEntityValueTransformer,queryCql);
                if(queryRes != null){
                    List<RelationEntityValue> resultEntitiesValues = (List<RelationEntityValue>)queryRes;
                    commonRelationEntitiesAttributesRetrieveResultImpl.addRelationEntitiesAttributes(resultEntitiesValues);
                    commonRelationEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonRelationEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonRelationEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return relationKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
