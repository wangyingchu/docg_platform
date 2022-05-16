package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Neo4JCrossKindDataOperatorImpl implements CrossKindDataOperator {

    private CoreRealm coreRealm;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(CrossKindDataOperator.class);

    public Neo4JCrossKindDataOperatorImpl(CoreRealm coreRealm){
        this.coreRealm = coreRealm;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public List<RelationEntity> getRelationsOfConceptionEntityPair(List<String> conceptionEntityUIDs) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.cover/
        */
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 2){
            logger.error("At least two conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least two conception entity UID is required");
            throw e;
        }

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "with collect(targetNodes) as nodes\n" +
                "CALL apoc.algo.cover(nodes)\n" +
                "YIELD rel\n" +
                "RETURN startNode(rel) as startNode, rel as operationResult, endNode(rel) as endNode;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
            return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public long removeRelationsOfConceptionEntityPair(List<String> conceptionEntityPairUIDs, String relationKind) throws CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.cover/
        */
        if(conceptionEntityPairUIDs == null || conceptionEntityPairUIDs.size() < 2){
            logger.error("At least two conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least two conception entity UID is required");
            throw e;
        }

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityPairUIDs.toString()+"\n"+
                "with collect(targetNodes) as nodes\n" +
                "CALL apoc.algo.cover(nodes)\n" +
                "YIELD rel\n" +
                "RETURN startNode(rel) as startNode, rel as operationResult, endNode(rel) as endNode;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
            if(relationEntityList != null){
                List<RelationEntity> resultRelationEntities = (List<RelationEntity>)relationEntityList;
                List<String> relationsForDeleteList = new ArrayList<>();
                for(RelationEntity currentRelationEntity:resultRelationEntities){
                    if(relationKind != null){
                        if(currentRelationEntity.getRelationKindName().equals(relationKind)){
                            relationsForDeleteList.add(currentRelationEntity.getRelationEntityUID());
                        }

                    }else{
                        relationsForDeleteList.add(currentRelationEntity.getRelationEntityUID());
                    }
                }
                if(resultRelationEntities.size() > 0){
                    cypherProcedureString = "MATCH ()-[r]->() WHERE id(r) IN "+relationsForDeleteList.toString()+
                            " DELETE r";
                    logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
                    workingGraphOperationExecutor.executeWrite(new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            return null;
                        }
                    },cypherProcedureString);
                }
                return relationsForDeleteList.size();
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return 0;
    }

    @Override
    public List<RelationEntity> getRelationEntitiesByUIDs(List<String> relationEntityUIDs) throws CoreRealmServiceEntityExploreException {
        if(relationEntityUIDs == null || relationEntityUIDs.size() < 1){
            logger.error("At least one relation entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one relation entity UID is required");
            throw e;
        }

        String cypherProcedureString = "MATCH ()-[r]->()\n" +
                "WHERE id(r) IN "+relationEntityUIDs.toString()+"\n" +
                "RETURN DISTINCT r as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
            return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<ConceptionEntity> getConceptionEntitiesByUIDs(List<String> conceptionEntityUIDs) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "RETURN DISTINCT targetNodes as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            return conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Double computeConceptionEntityPairTopologySimilarity(String conceptionEntityAUID, String conceptionEntityBUID,
                                                                TopologySimilarityComputeAlgorithm topologySimilarityComputeAlgorithm,
                                                                TopologySimilarityComputeDirection topologySimilarityComputeDirection,
                                                                String relationKindForCompute) throws CoreRealmServiceEntityExploreException,CoreRealmServiceRuntimeException {
        if(conceptionEntityAUID == null || conceptionEntityBUID == null){
            logger.error("Both conception entityA and conception entityB UID are required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("Both conception entityA and conception entityB UID are required");
            throw e;
        }

        String neo4jTopologySimilarityComputeAlgorithm = "";
        switch (topologySimilarityComputeAlgorithm){
            case AdamicAdar:
                /*
                Example:
                https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/adamic-adar/
                */
                neo4jTopologySimilarityComputeAlgorithm = "gds.alpha.linkprediction.adamicAdar";
                break;
            case CommonNeighbors:
                /*
                Example:
                https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/common-neighbors/
                */
                neo4jTopologySimilarityComputeAlgorithm = "gds.alpha.linkprediction.commonNeighbors";
                break;
            case ResourceAllocation:
                /*
                Example:
                https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/resource-allocation/
                */
                neo4jTopologySimilarityComputeAlgorithm = "gds.alpha.linkprediction.resourceAllocation";
                break;
            case TotalNeighbors:
                /*
                Example:
                https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/total-neighbors/
                */
                neo4jTopologySimilarityComputeAlgorithm = "gds.alpha.linkprediction.totalNeighbors";
                break;
            case PreferentialAttachment:
                /*
                Example:
                https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/preferential-attachment/
                */
                neo4jTopologySimilarityComputeAlgorithm = "gds.alpha.linkprediction.preferentialAttachment";
        }
        String relationshipQueryString = relationKindForCompute != null ?
                "relationshipQuery: '"+relationKindForCompute+"'," : "";

        String directionQueryString = topologySimilarityComputeDirection != null ?
                "direction: '"+topologySimilarityComputeDirection+"'":
                "direction: '"+TopologySimilarityComputeDirection.BOTH+"'";

        String cypherProcedureString = "MATCH (nodeA) WHERE id(nodeA)= " + conceptionEntityAUID+"\n" +
                        "MATCH (nodeB) WHERE id(nodeB)= " + conceptionEntityBUID+"\n" +
                        "RETURN "+neo4jTopologySimilarityComputeAlgorithm+"(nodeA, nodeB, {" +
                relationshipQueryString + directionQueryString+
                        "}) AS score";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer<Double> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    double score = nodeRecord.get("score").asNumber().doubleValue();
                    return score;
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            return queryResponse != null ? (Double)queryResponse : null;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<ConceptionEntityValue> getSingleValueConceptionEntityAttributesByUIDs(List<String> conceptionEntityUIDs, List<String> attributeNames) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }
        if(attributeNames == null || attributeNames.size() < 1){
            logger.error("At least one attribute name is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one attribute name is required");
            throw e;
        }

        String cypherProcedureString = CypherBuilder.matchAttributesWithNodeIDs(conceptionEntityUIDs,attributeNames);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
            getListConceptionEntityValueTransformer.setUseIDMatchLogic(true);
            Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer,cypherProcedureString);
            if(resEntityRes != null){
                List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
               return resultEntitiesValues;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<RelationEntityValue> getSingleValueRelationEntityAttributesByUIDs(List<String> relationEntityUIDs, List<String> attributeNames) throws CoreRealmServiceEntityExploreException {
        if(relationEntityUIDs == null || relationEntityUIDs.size() < 1){
            logger.error("At least one relation entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one relation entity UID is required");
            throw e;
        }
        if(attributeNames == null || attributeNames.size() < 1){
            logger.error("At least one attribute name is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one attribute name is required");
            throw e;
        }

        String cypherProcedureString = CypherBuilder.matchRelationsWithUIDs(relationEntityUIDs);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityValueTransformer getListRelationEntityValueTransformer =
                    new GetListRelationEntityValueTransformer(null,attributeNames);
            Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationEntityValueTransformer,cypherProcedureString);
            if(queryRes != null){
                List<RelationEntityValue> resultEntitiesValues = (List<RelationEntityValue>)queryRes;
                return resultEntitiesValues;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public EntitiesOperationResult fuseConceptionKindsAttributes(String fuseSourceKindName, String sourceKindMatchAttributeName,
                  List<String> attributesForFusion, String fuseTargetKindName, String targetKindMatchAttributeName) throws CoreRealmServiceEntityExploreException {
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();

        QueryParameters recordNumberSettingQueryParameters = new QueryParameters();
        recordNumberSettingQueryParameters.setResultNumber(100000000);

        ConceptionKind sourceNeo4JConceptionKindImpl = this.coreRealm.getConceptionKind(fuseSourceKindName);
        attributesForFusion.add(sourceKindMatchAttributeName);
        ConceptionEntitiesAttributesRetrieveResult sourceRetrieveResult = sourceNeo4JConceptionKindImpl.getSingleValueEntityAttributesByAttributeNames(attributesForFusion,recordNumberSettingQueryParameters);

        ConceptionKind targetNeo4JConceptionKindImpl = this.coreRealm.getConceptionKind(fuseTargetKindName);
        List<String> targetKindMatchAttList = new ArrayList<>();
        targetKindMatchAttList.add(targetKindMatchAttributeName);
        ConceptionEntitiesAttributesRetrieveResult targetRetrieveResult = targetNeo4JConceptionKindImpl.getSingleValueEntityAttributesByAttributeNames(targetKindMatchAttList,recordNumberSettingQueryParameters);

        List<ConceptionEntityValue> targetEntityValues = targetRetrieveResult.getConceptionEntityValues();
        HashMultimap<Object,String> targetEntitiesValueMap = HashMultimap.create();
        for(ConceptionEntityValue currentConceptionEntityValue : targetEntityValues){
            Map<String, Object> attributesValueMap = currentConceptionEntityValue.getEntityAttributesValue();
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            if(attributesValueMap.containsKey(targetKindMatchAttributeName)){
                targetEntitiesValueMap.put(attributesValueMap.get(targetKindMatchAttributeName),entityUID);
            }
        }

        List<Map<String,Object>> targetConceptionKindUpdateDataList = new ArrayList<>();
        String targetConceptionEntityUIDKey = "DOCG_CONCEPTION_ENTITY_UID";

        List<ConceptionEntityValue> sourceEntityValues = sourceRetrieveResult.getConceptionEntityValues();
        for(ConceptionEntityValue currentConceptionEntityValue:sourceEntityValues){
            Map<String, Object> attributesValue = currentConceptionEntityValue.getEntityAttributesValue();
            if(attributesValue.containsKey(sourceKindMatchAttributeName)){
                Object matchValue = attributesValue.get(sourceKindMatchAttributeName);
                Set<String> targetConceptionEntityUIDs = targetEntitiesValueMap.get(matchValue);
                if(targetConceptionEntityUIDs != null && targetConceptionEntityUIDs.size()>0){
                    attributesValue.remove(sourceKindMatchAttributeName);
                    for(String currentTargetEntityUID:targetConceptionEntityUIDs){
                        Map<String,Object> currentEntityAttributesMap = new HashMap<>();
                        currentEntityAttributesMap.putAll(attributesValue);
                        currentEntityAttributesMap.put(targetConceptionEntityUIDKey,currentTargetEntityUID);
                        targetConceptionKindUpdateDataList.add(currentEntityAttributesMap);
                        commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(currentTargetEntityUID);
                    }
                }
            }
        }

        Map<String,Object> updateResult = BatchDataOperationUtil.batchAddNewOrUpdateEntityAttributes
                (targetConceptionEntityUIDKey,targetConceptionKindUpdateDataList, BatchDataOperationUtil.CPUUsageRate.Middle);
        long totalSuccessCount = 0;
        Set<String> resultKeySet = updateResult.keySet();
        for(String currentKey:resultKeySet){
            if(!currentKey.equals("StartTime")&!currentKey.equals("FinishTime")){
                long currentSuccessfulCount = (Long)updateResult.get(currentKey);
                totalSuccessCount = totalSuccessCount + currentSuccessfulCount;
            }
        }
        commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(totalSuccessCount);
        commonEntitiesOperationResultImpl.getOperationStatistics().
                setOperationSummary("fuseConceptionKindsAttributes operation for conceptionKind "+fuseTargetKindName+" success.");
        commonEntitiesOperationResultImpl.finishEntitiesOperation();
        return commonEntitiesOperationResultImpl;
    }

    @Override
    public EntitiesOperationResult joinEntitiesToConceptionKinds(String sourceKindName, AttributesParameters attributesParameters, String[] newKindNames) throws CoreRealmServiceEntityExploreException {
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(false);
        queryParameters.setResultNumber(100);
        if (attributesParameters != null) {
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
        }

        String entityQueryCQL = CypherBuilder.matchNodesWithQueryParameters(sourceKindName,queryParameters, CypherBuilder.CypherFunctionType.ID);
        entityQueryCQL = entityQueryCQL.replace("RETURN id("+CypherBuilder.operationResultName+") LIMIT 100","");

        String labelModifyText = CypherBuilder.operationResultName;
        for(String currentLabel:newKindNames){
            labelModifyText = labelModifyText+ ":"+currentLabel;
        }
        entityQueryCQL= entityQueryCQL+ "SET "+labelModifyText+" RETURN id("+CypherBuilder.operationResultName+")";
        logger.debug("Generated Cypher Statement: {}", entityQueryCQL);

        List<String> resultEntityUIDsList = null;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListEntityUIDTransformer getListEntityUIDTransformer = new GetListEntityUIDTransformer();
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListEntityUIDTransformer,entityQueryCQL);
            if(queryRes != null){
                resultEntityUIDsList = (List<String>)queryRes;
                commonEntitiesOperationResultImpl.getSuccessEntityUIDs().addAll(resultEntityUIDsList);
                commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(resultEntityUIDsList.size());
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        commonEntitiesOperationResultImpl.getOperationStatistics().
                setOperationSummary("joinEntitiesToConceptionKinds operation for conceptionKind "+sourceKindName+" success.");
        commonEntitiesOperationResultImpl.finishEntitiesOperation();
        return commonEntitiesOperationResultImpl;
    }

    @Override
    public EntitiesOperationResult retreatEntitiesFromConceptionKind(String sourceKindName, AttributesParameters attributesParameters, String kindName) throws CoreRealmServiceEntityExploreException {
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(false);
        queryParameters.setResultNumber(100);
        if (attributesParameters != null) {
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
        }

        String entityQueryCQL = CypherBuilder.matchNodesWithQueryParameters(sourceKindName,queryParameters, CypherBuilder.CypherFunctionType.ID);
        entityQueryCQL = entityQueryCQL.replace("RETURN id("+CypherBuilder.operationResultName+") LIMIT 100","");

        entityQueryCQL= entityQueryCQL+ "REMOVE "+CypherBuilder.operationResultName+":"+kindName+" RETURN id("+CypherBuilder.operationResultName+")";
        logger.debug("Generated Cypher Statement: {}", entityQueryCQL);

        List<String> resultEntityUIDsList = null;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListEntityUIDTransformer getListEntityUIDTransformer = new GetListEntityUIDTransformer();
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListEntityUIDTransformer,entityQueryCQL);
            if(queryRes != null){
                resultEntityUIDsList = (List<String>)queryRes;
                commonEntitiesOperationResultImpl.getSuccessEntityUIDs().addAll(resultEntityUIDsList);
                commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(resultEntityUIDsList.size());
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        commonEntitiesOperationResultImpl.getOperationStatistics().
                setOperationSummary("retreatEntitiesFromConceptionKind operation for conceptionKind "+sourceKindName+" success.");
        commonEntitiesOperationResultImpl.finishEntitiesOperation();
        return commonEntitiesOperationResultImpl;
    }

    @Override
    public EntitiesOperationResult mergeEntitiesToConceptionKind(String sourceKindName, AttributesParameters attributesParameters, String relationKindName, RelationDirection relationDirection, String targetConceptionKindName) throws CoreRealmServiceEntityExploreException {
        if(relationDirection == null || RelationDirection.TWO_WAY.equals(relationDirection)){
            logger.error("relationDirection must set and can't be TWO_WAY.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("relationDirection must set and can't be TWO_WAY.");
            throw exception;
        }
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(false);
        queryParameters.setResultNumber(100);
        if (attributesParameters != null) {
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
        }

        String entityQueryCQL = CypherBuilder.matchNodesWithQueryParameters(sourceKindName,queryParameters, CypherBuilder.CypherFunctionType.ID);
        entityQueryCQL = entityQueryCQL.replace("RETURN id("+CypherBuilder.operationResultName+") LIMIT 100","");

        String relationTypeMatchingStr = relationKindName != null ? "r:"+relationKindName:"r";
        String relationPathMatchingStr = "-[r]-";
        if(relationDirection == null){
            relationPathMatchingStr = "-["+relationTypeMatchingStr+"]-";
        }else{
            switch(relationDirection){
                case FROM: relationPathMatchingStr = "-["+relationTypeMatchingStr+"]->"; break;
                case TO: relationPathMatchingStr = "<-["+relationTypeMatchingStr+"]-"; break;
                case TWO_WAY: relationPathMatchingStr = "-["+relationTypeMatchingStr+"]-";
            }
        }
        String targetKindMatchingStr = targetConceptionKindName != null ? "target:"+targetConceptionKindName : "target";
        String cypherProcedureString = "MATCH (source)"+relationPathMatchingStr+"("+targetKindMatchingStr+") WHERE id(source) IN sourceUIDs RETURN r AS "+CypherBuilder.operationResultName;
        entityQueryCQL = entityQueryCQL+" \n"+
                "WITH collect(id("+CypherBuilder.operationResultName+")) AS sourceUIDs"+" \n"+
                cypherProcedureString;
        logger.debug("Generated Cypher Statement: {}", entityQueryCQL);
        List<RelationEntityValue> resultEntitiesValues = null;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityValueTransformer getListRelationEntityValueTransformer =
                    new GetListRelationEntityValueTransformer(relationKindName,null);
            Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationEntityValueTransformer,entityQueryCQL);
            if(queryRes != null){
                resultEntitiesValues = (List<RelationEntityValue>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        int degreeOfParallelism = BatchDataOperationUtil.calculateRuntimeCPUCoresByUsageRate(BatchDataOperationUtil.CPUUsageRate.High);
        int singlePartitionSize = (resultEntitiesValues.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(resultEntitiesValues, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();

        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<RelationEntityValue> currentRelationEntityValueList:rsList){
            MergeEntitiesToConceptionKindThread mergeEntitiesToConceptionKindThread =  new MergeEntitiesToConceptionKindThread(currentRelationEntityValueList,threadReturnDataMap);
            executor.execute(mergeEntitiesToConceptionKindThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<RelationEntityValue> getRelationEntityAttributesByRelatedRelationKind(List<String> conceptionEntityUIDs, String relationKind, List<String> returnedAttributeList,RelationDirection relationDirection, String targetConceptionKindName) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }
        String relationTypeMatchingStr = relationKind != null ? "r:"+relationKind:"r";
        String relationPathMatchingStr = "-[r]-";
        if(relationDirection == null){
            relationPathMatchingStr = "-["+relationTypeMatchingStr+"]-";
        }else{
            switch(relationDirection){
                case FROM: relationPathMatchingStr = "-["+relationTypeMatchingStr+"]->"; break;
                case TO: relationPathMatchingStr = "<-["+relationTypeMatchingStr+"]-"; break;
                case TWO_WAY: relationPathMatchingStr = "-["+relationTypeMatchingStr+"]-";
            }
        }
        String targetKindMatchingStr = targetConceptionKindName != null ? "target:"+targetConceptionKindName : "target";
        String cypherProcedureString = "MATCH (source)"+relationPathMatchingStr+"("+targetKindMatchingStr+") WHERE id(source) IN "+ conceptionEntityUIDs.toString()+" RETURN r AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityValueTransformer getListRelationEntityValueTransformer =
                    new GetListRelationEntityValueTransformer(relationKind,returnedAttributeList);
            Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationEntityValueTransformer,cypherProcedureString);
            if(queryRes != null){
                List<RelationEntityValue> resultEntitiesValues = (List<RelationEntityValue>)queryRes;
                return resultEntitiesValues;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    private class MergeEntitiesToConceptionKindThread implements Runnable {
        private List<RelationEntityValue> relationEntityValueList;
        private Map<String,Object> threadReturnDataMap;
        public MergeEntitiesToConceptionKindThread(List<RelationEntityValue> relationEntityValueList,Map<String,Object> threadReturnDataMap){
            this.relationEntityValueList = relationEntityValueList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            //System.out.println(this.relationEntityValueList);
        }
    }
}
