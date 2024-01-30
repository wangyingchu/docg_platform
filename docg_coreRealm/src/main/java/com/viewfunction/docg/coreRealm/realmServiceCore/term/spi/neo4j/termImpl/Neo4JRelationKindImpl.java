package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonRelationEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonRelationEntitiesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    public boolean updateRelationKindDesc(String kindDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, kindDesc);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.relationKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.relationKindUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(kindDesc)){
                this.relationKindDesc = kindDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
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
                        null,null,false,queryParameters, CypherBuilder.CypherFunctionType.COUNT);

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
                        null,null,false,queryParameters,null);
                GetListRelationEntityTransformer getListRelationEntityTransformer =
                        new GetListRelationEntityTransformer(this.relationKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor(),queryParameters.isDistinctMode());
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
                        null,null,false,queryParameters,null);
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

    @Override
    public RelationEntity getEntityByUID(String relationEntityUID) {
        if (relationEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchRelationWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(relationEntityUID), null, null);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (this.relationKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryCql);
                return resEntityRes != null ? (RelationEntity) resEntityRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public RelationDegreeDistributionInfo computeRelationDegreeDistribution(RelationDirection relationDirection) {
        String relationKindNameAndDirection = this.relationKindName;
        if(relationDirection != null){
            switch(relationDirection){
                case FROM: relationKindNameAndDirection = this.relationKindName+">";
                    break;
                case TO: relationKindNameAndDirection = "<"+this.relationKindName;
                    break;
                case TWO_WAY: relationKindNameAndDirection = this.relationKindName;
            }
        }

        String cypherProcedureString = "CALL apoc.stats.degrees(\""+relationKindNameAndDirection+"\");";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer<RelationDegreeDistributionInfo> entityRelationDegreeDataTransformer = new DataTransformer() {
            @Override
            public RelationDegreeDistributionInfo transformResult(Result result) {
                if(result.hasNext()){
                    Record nodeRecord = result.next();
                    String type = ""+nodeRecord.get("type").asString();
                    long total = nodeRecord.get("total").asLong();
                    long p50 = nodeRecord.get("p50").asLong();
                    long p75 = nodeRecord.get("p75").asLong();
                    long p90 = nodeRecord.get("p90").asLong();
                    long p95 = nodeRecord.get("p95").asLong();
                    long p99 = nodeRecord.get("p99").asLong();
                    long p999 = nodeRecord.get("p999").asLong();
                    long max = nodeRecord.get("max").asLong();
                    long min = nodeRecord.get("min").asLong();
                    float mean = nodeRecord.get("mean").asNumber().floatValue();

                    RelationDegreeDistributionInfo relationDegreeDistributionInfo = new RelationDegreeDistributionInfo(
                            type, relationDirection,total,p50,p75,p90,p95,p99,p999,max,min,mean
                    );
                    return relationDegreeDistributionInfo;
                }
                return null;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object resEntityRes = workingGraphOperationExecutor.executeRead(entityRelationDegreeDataTransformer, cypherProcedureString);
            return resEntityRes != null ? (RelationDegreeDistributionInfo) resEntityRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getConceptionKindsRelationStatistics() {
        Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoList = new HashSet<>();
        String cypherProcedureString = "CALL db.schema.visualization()";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer queryResultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {

                    if(result.hasNext()){
                        Record currentRecord = result.next();
                        List nodesList = currentRecord.get("nodes").asList();

                        Map<String,String> conceptionKindMetaInfoMap = new HashMap<>();
                        for(Object currentNode:nodesList){
                            Node currentNeo4JNode = (Node)currentNode;
                            conceptionKindMetaInfoMap.put(""+currentNeo4JNode.id(),currentNeo4JNode.get("name").asString());
                        }
                        List relationList =  currentRecord.get("relationships").asList();
                        for(Object currenRelation:relationList){
                            Relationship currentNeo4JRelation = (Relationship)currenRelation;
                            String currentRelationKindName = currentNeo4JRelation.type();
                            if(relationKindName.equals(currentRelationKindName)){
                                String startConceptionKindName = conceptionKindMetaInfoMap.get(""+currentNeo4JRelation.startNodeId());
                                String targetConceptionKindName = conceptionKindMetaInfoMap.get(""+currentNeo4JRelation.endNodeId());
                                //apoc.meta.graphSample does not filter away non-existing paths.so count record maybe return wrong result
                                //int relationEntityCount = currentNeo4JRelation.get("count").asInt();
                                boolean relationExist = checkRelationEntitiesExist(workingGraphOperationExecutor,startConceptionKindName,targetConceptionKindName,currentRelationKindName);
                                if(relationExist){
                                    long existingRelCount = checkRelationEntitiesCount(workingGraphOperationExecutor,startConceptionKindName,targetConceptionKindName,currentRelationKindName);
                                    if(existingRelCount != 0){
                                        conceptionKindCorrelationInfoList.add(new ConceptionKindCorrelationInfo(startConceptionKindName,
                                                targetConceptionKindName,currentRelationKindName,existingRelCount)
                                        );
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(queryResultDataTransformer,cypherProcedureString);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return conceptionKindCorrelationInfoList;
    }

    @Override
    public Set<RelationEntity> getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH p=()-[r:"+this.relationKindName+"]->() RETURN apoc.coll.randomItems(COLLECT(r),"+entitiesCount+") AS "+CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            RandomItemsRelationEntitySetDataTransformer randomItemsRelationEntitySetDataTransformer = new RandomItemsRelationEntitySetDataTransformer(workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsRelationEntitySetDataTransformer,queryCql);
            if(queryRes != null){
                Set<RelationEntity> resultRelationEntityList = (Set<RelationEntity>)queryRes;
                return resultRelationEntityList;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<RelationEntity> getRandomEntities(AttributesParameters attributesParameters, boolean isDistinctMode, int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
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
                String replaceContent = isDistinctMode ? "RETURN count(DISTINCT "+CypherBuilder.operationResultName+") SKIP 0 LIMIT 100000000" :
                        "RETURN count("+CypherBuilder.operationResultName+") SKIP 0 LIMIT 100000000";
                String newContent = isDistinctMode ? "RETURN apoc.coll.randomItems(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",false) AS " +CypherBuilder.operationResultName:
                        "RETURN apoc.coll.randomItems(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",true) AS " +CypherBuilder.operationResultName;
                queryCql = queryCql.replace(replaceContent,newContent);
                logger.debug("Generated Cypher Statement: {}", queryCql);
                RandomItemsRelationEntitySetDataTransformer randomItemsRelationEntitySetDataTransformer = new RandomItemsRelationEntitySetDataTransformer(workingGraphOperationExecutor);
                Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsRelationEntitySetDataTransformer,queryCql);
                if(queryRes != null){
                    Set<RelationEntity> resultRelationEntityList = (Set<RelationEntity>)queryRes;
                    return resultRelationEntityList;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return null;
        }else{
            return getRandomEntities(entitiesCount);
        }
    }

    @Override
    public EntitiesOperationStatistics setKindScopeAttributes(Map<String, Object> attributes) throws CoreRealmServiceRuntimeException {
        if(attributes == null || attributes.size() ==0){
            logger.error("attributes Map must have at least 1 attribute value.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("attributes Map must have at least 1 attribute value.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.setRelationKindProperties(this.relationKindName,attributes);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,queryCql);
            if(queryRes != null) {
                Long operationResult =(Long)queryRes;
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount(operationResult);
                entitiesOperationStatistics.setOperationSummary("setKindScopeAttributes operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public long purgeRelationsOfSelfAttachedConceptionEntities() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH p=(n1)-[r:"+this.relationKindName+"]->(n2) WHERE id(n1) = id(n2) delete r return count(r) AS " + CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object queryRes = workingGraphOperationExecutor.executeWrite(GetLongFormatAggregatedReturnValueTransformer,queryCql);
            if(queryRes != null) {
                Long operationResult =(Long)queryRes;
                return operationResult;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return 0;
    }

    @Override
    public boolean deleteEntity(String relationEntityUID) throws CoreRealmServiceRuntimeException {
        if(relationEntityUID != null){
            RelationEntity targetRelationEntity = this.getEntityByUID(relationEntityUID);
            if(targetRelationEntity != null){
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                try{
                    String deleteCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationEntityUID),null,null);
                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer =
                            new GetSingleRelationEntityTransformer(this.relationKindName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object deletedEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, deleteCql);
                    if(deletedEntityRes == null){
                        throw new CoreRealmServiceRuntimeException();
                    }else{
                        return true;
                    }
                }finally {
                    this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
                }
            }else{
                logger.error("RelationKind {} does not contains entity with UID {}.", this.relationKindName, relationEntityUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("RelationKind " + this.relationKindName + " does not contains entity with UID " + relationEntityUID + ".");
                throw exception;
            }
        }
        return false;
    }

    @Override
    public EntitiesOperationResult deleteEntities(List<String> relationEntityUIDs) throws CoreRealmServiceRuntimeException {
        if(relationEntityUIDs != null && relationEntityUIDs.size()>0){
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            boolean countFail = false;
            for(String currentConceptionEntityUID:relationEntityUIDs) {
                RelationEntity targetRelationEntity = this.getEntityByUID(currentConceptionEntityUID);
                if(targetRelationEntity != null){
                    try {
                        boolean deleteCurrentEntityResult = deleteEntity(currentConceptionEntityUID);
                        if(deleteCurrentEntityResult){
                            commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(currentConceptionEntityUID);
                            commonEntitiesOperationResultImpl.getOperationStatistics().increaseSuccessCount();
                        }else{
                            commonEntitiesOperationResultImpl.getOperationStatistics().getFailItemsCount();
                        }
                    } catch (CoreRealmServiceRuntimeException e) {
                        e.printStackTrace();
                        commonEntitiesOperationResultImpl.getOperationStatistics().getFailItemsCount();
                        logger.error("Exception occurred during delete entity with UID {} of RelationKind {}.", currentConceptionEntityUID , this.relationKindName);
                    }
                }else{
                    commonEntitiesOperationResultImpl.getOperationStatistics().increaseFailCount();
                    countFail = true;
                }
            }
            if(countFail){
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("deleteEntities operation for relationKind "+this.relationKindName+" partial success.");
            }else{
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("deleteEntities operation for relationKind "+this.relationKindName+" success.");
            }
            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics removeEntityAttributes(Set<String> attributeNames) throws CoreRealmServiceRuntimeException {
        if(attributeNames == null || attributeNames.size() ==0){
            logger.error("attributeNames must have at least 1 attribute name.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("attributeNames must have at least 1 attribute name.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        //https://neo4j.com/docs/apoc/current/overview/apoc.create/apoc.create.removeRelProperties/
        String attributeNameStr = "[";
        for(String currentAttribute:attributeNames){
            attributeNameStr = attributeNameStr+"'"+currentAttribute+"'"+",";
        }
        attributeNameStr = attributeNameStr.substring(0,attributeNameStr.length()-1);
        attributeNameStr = attributeNameStr+"]";

        String queryCql = "MATCH ()-[n:`"+this.relationKindName+"`]->() WITH collect(n) AS entities\n" +
                "CALL apoc.create.removeRelProperties(entities, "+attributeNameStr+")\n" +
                "YIELD rel\n" +
                "RETURN count(rel) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount((Long) countConceptionEntitiesRes);
                entitiesOperationStatistics.setOperationSummary("removeEntityAttributes operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToIntType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH ()-[rel:`"+this.relationKindName+"`]->()\n" +
                "SET rel."+attributeName+" = toIntegerOrNull(rel."+attributeName+") RETURN count(rel) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToIntType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToFloatType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH ()-[rel:`"+this.relationKindName+"`]->()\n" +
                "SET rel."+attributeName+" = toFloatOrNull(rel."+attributeName+") RETURN count(rel) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToFloatType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToBooleanType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH ()-[rel:`"+this.relationKindName+"`]->()\n" +
                "SET rel."+attributeName+" = toBooleanOrNull(rel."+attributeName+") RETURN count(rel) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToBooleanType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToStringType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH ()-[rel:`"+this.relationKindName+"`]->()\n" +
                "SET rel."+attributeName+" = toString(rel."+attributeName+") RETURN count(rel) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToStringType operation success");
        return entitiesOperationStatistics;
    }

    private long executeEntitiesOperationWithCountResponse(String cql){
        long operationResultCount = 0;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, cql);
            if (countConceptionEntitiesRes != null) {
                operationResultCount =(Long) countConceptionEntitiesRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return operationResultCount;
    }

    private class RandomItemsRelationEntitySetDataTransformer implements DataTransformer<Set<RelationEntity>>{
        GraphOperationExecutor workingGraphOperationExecutor;
        public RandomItemsRelationEntitySetDataTransformer(GraphOperationExecutor workingGraphOperationExecutor){
            this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        }
        @Override
        public Set<RelationEntity> transformResult(Result result) {
            Set<RelationEntity> relationEntitySet = new HashSet<>();
            if(result.hasNext()){
                List<Value> resultList = result.next().values();
                if(resultList.size() > 0){
                    List<Object> nodeObjList = resultList.get(0).asList();
                    for(Object currentNodeObj : nodeObjList){
                        Relationship resultRelationship = (Relationship)currentNodeObj;
                        boolean isMatchedRelationKind = relationKindName.equals(resultRelationship.type());
                        if(isMatchedRelationKind){
                            long relationUID = resultRelationship.id();
                            String relationEntityUID = ""+relationUID;
                            String fromEntityUID = ""+resultRelationship.startNodeId();
                            String toEntityUID = ""+resultRelationship.endNodeId();
                            Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                                    new Neo4JRelationEntityImpl(relationKindName,relationEntityUID,fromEntityUID,toEntityUID);
                            neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                            relationEntitySet.add(neo4jRelationEntityImpl);
                        }
                    }
                }
            }
            return relationEntitySet;
        }
    }

    private long checkRelationEntitiesCount(GraphOperationExecutor workingGraphOperationExecutor,String sourceConceptionKindName,
                                            String targetConceptionKindName,String relationKindName){
        String cql = "MATCH p=(source:"+sourceConceptionKindName+")-[r:"+relationKindName+"]->(target:"+targetConceptionKindName+") RETURN count(r) AS operationResult";
        GetLongFormatReturnValueTransformer GetLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();
        Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatReturnValueTransformer,cql);
        if(queryRes != null){
            return (Long)queryRes;
        }
        return 0;
    }

    private boolean checkRelationEntitiesExist(GraphOperationExecutor workingGraphOperationExecutor,String sourceConceptionKindName,
                                               String targetConceptionKindName,String relationKindName){
        String cql = "MATCH p=(source:"+sourceConceptionKindName+")-[r:"+relationKindName+"]->(target:"+targetConceptionKindName+") RETURN r AS operationResult LIMIT 1";
        logger.debug("Generated Cypher Statement: {}", cql);
        DataTransformer<Boolean> dataTransformer = new DataTransformer<>() {
            @Override
            public Boolean transformResult(Result result) {
                boolean relationEntitiesExist = false;
                int resultNumCount = result.list().size();
                if(resultNumCount == 0){
                    relationEntitiesExist = false;
                }else{
                    relationEntitiesExist = true;
                }
                return relationEntitiesExist;
            }
        };
        Object queryRes = workingGraphOperationExecutor.executeRead(dataTransformer,cql);
        return queryRes != null ? ((Boolean)queryRes).booleanValue():false;
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
