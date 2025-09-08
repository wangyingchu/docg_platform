package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.PathEntitiesSequenceMatchPattern;
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
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonTimeScaleEventsRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntitiesSequence;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
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
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor(),true);
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
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor(),true);
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

        String cypherProcedureString = "MATCH (source)-[r]->(target)\n" +
                "WHERE id(r) IN "+relationEntityUIDs.toString()+"\n" +
                "RETURN DISTINCT r as operationResult,source as sourceNode, target as targetNode";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor(),false);
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
            return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public EntitiesOperationStatistics deleteRelationEntitiesByUIDs(List<String> relationEntityUIDs) throws CoreRealmServiceEntityExploreException {
        if(relationEntityUIDs == null || relationEntityUIDs.size() < 1){
            logger.error("At least one relation entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one relation entity UID is required");
            throw e;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String cypherProcedureString = "CALL apoc.periodic.commit(\n" +
                "         'MATCH (source)-[r]->(target) WHERE id(r) IN "+ relationEntityUIDs.toString()+" WITH r LIMIT $limit DELETE r RETURN count(*)',\n" +
                "         {limit: 10000}\n" +
                "       )\n" +
                "       YIELD updates, executions, runtime, batches\n" +
                "       RETURN updates, executions, runtime, batches;";
        //logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer,cypherProcedureString);

            cypherProcedureString = "MATCH (source)-[r]->(target)\n" +
                    "WHERE id(r) IN "+relationEntityUIDs.toString()+"\n" +
                    "RETURN DISTINCT COUNT(r) as operationResult";

            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();
            Object operationResult = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,cypherProcedureString);
            if(operationResult!= null){
                entitiesOperationStatistics.setSuccessItemsCount(relationEntityUIDs.size() - (Long)operationResult);
                entitiesOperationStatistics.setFailItemsCount((Long)operationResult);

            }
            entitiesOperationStatistics.setOperationSummary("deleteRelationEntitiesByUIDs operation success");
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics setRelationEntitiesAttributesByUIDs(List<String> relationEntityUIDs, Map<String, Object> attributes) throws CoreRealmServiceEntityExploreException {
        if(relationEntityUIDs == null || relationEntityUIDs.size() < 1){
            logger.error("At least one relation entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one relation entity UID is required");
            throw e;
        }
        if(attributes == null || attributes.isEmpty()){
            logger.error("At least one attribute value is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one attribute value is required");
            throw e;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.setRelationKindProperties("NOT_EXIST_RELATION_KIND",attributes);
            String queryByUIDListPart = "MATCH (source)-["+CypherBuilder.operationResultName+"]->(target) WHERE id("+CypherBuilder.operationResultName+") IN "+relationEntityUIDs.toString()+"\n";
            String queryByUIDsCql =queryCql.replace("MATCH (sourceNode)-["+CypherBuilder.operationResultName+":`NOT_EXIST_RELATION_KIND`]->(targetNode)",queryByUIDListPart);
            logger.debug("Generated Cypher Statement: {}", queryByUIDsCql);

            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,queryByUIDsCql);
            if(queryRes != null) {
                Long operationResult =(Long)queryRes;
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount(operationResult);
                entitiesOperationStatistics.setOperationSummary("setRelationEntitiesAttributesByUIDs operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
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
    public EntitiesOperationStatistics setConceptionEntitiesAttributesByUIDs(List<String> conceptionEntityUIDs, Map<String, Object> attributes) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }
        if(attributes == null || attributes.isEmpty()){
            logger.error("At least one attribute value is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one attribute value is required");
            throw e;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.setConceptionKindProperties("NOT_EXIST_CONCEPTION_KIND",attributes);
            String queryByUIDListPart = "MATCH ("+CypherBuilder.operationResultName+") WHERE id("+CypherBuilder.operationResultName+") IN " + conceptionEntityUIDs.toString()+"\n";
            String queryByUIDsCql =queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`NOT_EXIST_CONCEPTION_KIND`)",queryByUIDListPart);
            logger.debug("Generated Cypher Statement: {}", queryByUIDsCql);

            GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeWrite(GetLongFormatAggregatedReturnValueTransformer,queryByUIDsCql);
            if(queryRes != null) {
                Long operationResult =(Long)queryRes;
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount(operationResult);
                entitiesOperationStatistics.setOperationSummary("setConceptionEntitiesAttributesByUIDs operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics deleteConceptionEntitiesByUIDs(List<String> conceptionEntityUIDs) throws CoreRealmServiceEntityExploreException{
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String cypherProcedureString = "CALL apoc.periodic.commit(\n" +
                "         'MATCH (targetNodes) WHERE id(targetNodes) IN "+conceptionEntityUIDs.toString()+" WITH targetNodes LIMIT $limit DETACH DELETE targetNodes RETURN count(*)',\n" +
                "         {limit: 10000}\n" +
                "       )\n" +
                "       YIELD updates, executions, runtime, batches\n" +
                "       RETURN updates, executions, runtime, batches;";
        //logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer,cypherProcedureString);

            cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                    "RETURN COUNT(targetNodes) as operationResult";

            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();
            Object operationResult = workingGraphOperationExecutor.executeRead(getLongFormatReturnValueTransformer,cypherProcedureString);
            if(operationResult!= null){
                entitiesOperationStatistics.setSuccessItemsCount(conceptionEntityUIDs.size() - (Long)operationResult);
                entitiesOperationStatistics.setFailItemsCount((Long)operationResult);

            }
            entitiesOperationStatistics.setOperationSummary("deleteConceptionEntitiesByUIDs operation success");
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics removeConceptionEntitiesAttributesByUIDs(List<String> conceptionEntityUIDs, List<String> attributeNames) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() < 1){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }

        if(attributeNames == null || attributeNames.size() ==0){
            logger.error("attributeNames must have at least 1 attribute name.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("attributeNames must have at least 1 attribute name.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        //https://neo4j.com/docs/apoc/current/overview/apoc.create/apoc.create.removeProperties/
        String attributeNameStr = "[";
        for(String currentAttribute:attributeNames){
            attributeNameStr = attributeNameStr+"'"+currentAttribute+"'"+",";
        }
        attributeNameStr = attributeNameStr.substring(0,attributeNameStr.length()-1);
        attributeNameStr = attributeNameStr+"]";

        String queryCql = "MATCH (n) WHERE id(n) IN " + conceptionEntityUIDs.toString()+"\n"
                +"WITH collect(n) AS entities\n" +
                "CALL apoc.create.removeProperties(entities, "+attributeNameStr+")\n" +
                "YIELD node\n" +
                "RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes != null) {
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount((Long) countConceptionEntitiesRes);
                entitiesOperationStatistics.setOperationSummary("removeConceptionEntitiesAttributesByUIDs operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
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
        if(attributeNames != null){
            if(attributeNames.isEmpty()){
                logger.error("At least one attribute name is required");
                CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                e.setCauseMessage("At least one attribute name is required");
                throw e;
            }
        }

        String cypherProcedureString;
        if(attributeNames == null){
            cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                    "RETURN DISTINCT targetNodes as operationResult";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        }else{
            cypherProcedureString = CypherBuilder.matchAttributesWithNodeIDs(conceptionEntityUIDs,attributeNames);
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
            if(attributeNames != null){
                getListConceptionEntityValueTransformer.setUseIDMatchLogic(true);
            }else{
                getListConceptionEntityValueTransformer.setUseIDMatchLogic(false);
            }
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
        if(attributeNames != null){
            if(attributeNames.size() < 1){
                logger.error("At least one attribute name is required");
                CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                e.setCauseMessage("At least one attribute name is required");
                throw e;
            }
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
            logger.error("relationDirection must set and can't be TWO_WAY");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("relationDirection must set and can't be TWO_WAY");
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
            MergeEntitiesToConceptionKindThread mergeEntitiesToConceptionKindThread =  new MergeEntitiesToConceptionKindThread(currentRelationEntityValueList,relationDirection,threadReturnDataMap);
            executor.execute(mergeEntitiesToConceptionKindThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long wholeSuccessCount = 0;
        for (Map.Entry<String, Object> entry : threadReturnDataMap.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.startsWith("SUCCESSCOUNT_")){
                Integer threadSuccessCount = (Integer)value;
                wholeSuccessCount = wholeSuccessCount +threadSuccessCount.longValue();
            }
            if(key.startsWith("SUCCESSUIDS_")){
                List<String> successUIDs = (List<String>)value;
                commonEntitiesOperationResultImpl.getSuccessEntityUIDs().addAll(successUIDs);
            }
        }
        commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(wholeSuccessCount);
        commonEntitiesOperationResultImpl.getOperationStatistics().
                setOperationSummary("mergeEntitiesToConceptionKind operation success.");
        commonEntitiesOperationResultImpl.finishEntitiesOperation();
        return commonEntitiesOperationResultImpl;
    }

    @Override
    public List<RelationEntityValue> getRelationAttributesByEntitiesRelation(List<String> conceptionEntityUIDs, String relationKind, List<String> returnedAttributeList,RelationDirection relationDirection, String targetConceptionKindName) throws CoreRealmServiceEntityExploreException {
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

    @Override
    public List<RelationEntity> extractRelationsFromBridgeConceptionEntities(String sourceKindName,String targetKindName, String bridgeKindName,
                                                                             AttributesParameters attributesParameters,String sourceToBridgeRelationKindName,
                                                                             String bridgeToTargetRelationKindName, String sourceToTargetRelationKindName,
                                                                             boolean allowRepeat) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(sourceToTargetRelationKindName == null){
            logger.error("Param sourceToTargetRelationKindName in method createRelationEntitiesFromBridgeConceptionEntities is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param sourceToTargetRelationKindName in method createRelationEntitiesFromBridgeConceptionEntities is required");
            throw e1;
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(false);
        queryParameters.setResultNumber(100000000);
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
        String bridgeEntityQueryCQL = CypherBuilder.matchNodesWithQueryParameters(bridgeKindName,queryParameters, CypherBuilder.CypherFunctionType.ID);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListEntityUIDTransformer getListEntityUIDTransformer = new GetListEntityUIDTransformer();
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListEntityUIDTransformer,bridgeEntityQueryCQL);
            if(queryRes != null){
                List<String> resultEntityUIDsList = (List<String>)queryRes;

                String sourceNodesPart = sourceKindName == null ? "sourceNodes":"sourceNodes:"+sourceKindName;
                String targetNodesPart = targetKindName == null ? "targetNodes":"targetNodes:"+targetKindName;
                String sourceToBridgeRelPart = sourceToBridgeRelationKindName == null ? "r1":"r1:"+sourceToBridgeRelationKindName;
                String bridgeToTargetRelPart = bridgeToTargetRelationKindName == null ? "r2":"r2:"+bridgeToTargetRelationKindName;

                String createRelAccordingToAllowRepeatPart = allowRepeat ?
                        "CREATE (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes)\n " +
                                "WITH sourceNodes,targetNodes\n" +
                                "MATCH (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes) RETURN sToTRel AS operationResult" :
                        "MERGE (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes) RETURN sToTRel AS operationResult";
                String creatRelationCQL = "MATCH ("+sourceNodesPart+")-["+sourceToBridgeRelPart+"]->(middleNodes)-["+bridgeToTargetRelPart+"]->("+targetNodesPart+") WHERE id(middleNodes) IN "+resultEntityUIDsList.toString()+" \n" +
                        "WITH sourceNodes,targetNodes\n" +
                        createRelAccordingToAllowRepeatPart;
                logger.debug("Generated Cypher Statement: {}", creatRelationCQL);

                GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(sourceToTargetRelationKindName,workingGraphOperationExecutor,false);
                queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,creatRelationCQL);
                if(queryRes != null){
                    return (List<RelationEntity>)queryRes;
                }
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<RelationEntity> extractRelationsFromBridgeConceptionEntities(String sourceKindName, String targetKindName, List<String> bridgeConceptionEntityUIDs,
                                                                             String sourceToBridgeRelationKindName, String bridgeToTargetRelationKindName,
                                                                             String sourceToTargetRelationKindName, boolean allowRepeat) throws CoreRealmServiceRuntimeException {
        if(sourceToTargetRelationKindName == null){
            logger.error("Param sourceToTargetRelationKindName in method createRelationEntitiesFromBridgeConceptionEntities is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param sourceToTargetRelationKindName in method createRelationEntitiesFromBridgeConceptionEntities is required");
            throw e1;
        }
        if(bridgeConceptionEntityUIDs == null || bridgeConceptionEntityUIDs.size() == 0){
            logger.error("At lease one bridge conceptionEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one bridge conceptionEntityUID is required");
            throw e1;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String sourceNodesPart = sourceKindName == null ? "sourceNodes":"sourceNodes:"+sourceKindName;
            String targetNodesPart = targetKindName == null ? "targetNodes":"targetNodes:"+targetKindName;
            String sourceToBridgeRelPart = sourceToBridgeRelationKindName == null ? "r1":"r1:"+sourceToBridgeRelationKindName;
            String bridgeToTargetRelPart = bridgeToTargetRelationKindName == null ? "r2":"r2:"+bridgeToTargetRelationKindName;

            String createRelAccordingToAllowRepeatPart = allowRepeat ?
                    "CREATE (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes)\n " +
                            "WITH sourceNodes,targetNodes\n" +
                            "MATCH (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes) RETURN sToTRel AS operationResult" :
                    "MERGE (sourceNodes)-[sToTRel:"+sourceToTargetRelationKindName+"]->(targetNodes) RETURN sToTRel AS operationResult";
            String creatRelationCQL = "MATCH ("+sourceNodesPart+")-["+sourceToBridgeRelPart+"]->(middleNodes)-["+bridgeToTargetRelPart+"]->("+targetNodesPart+") WHERE id(middleNodes) IN "+bridgeConceptionEntityUIDs.toString()+" \n" +
                    "WITH sourceNodes,targetNodes\n" +
                    createRelAccordingToAllowRepeatPart;
            logger.debug("Generated Cypher Statement: {}", creatRelationCQL);

            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(sourceToTargetRelationKindName,workingGraphOperationExecutor,false);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,creatRelationCQL);
            if(queryRes != null){
                return (List<RelationEntity>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<ConceptionEntity> extractIntermediateConceptionEntitiesFromRelations(List<String> relationEntityUIDs, String intermediateConceptionKindName, String fromRelationKind, String toRelationKind) throws CoreRealmServiceRuntimeException{
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.extractNode/
        if(intermediateConceptionKindName == null){
            logger.error("Param intermediateConceptionKindName in method extractIntermediateConceptionEntitiesFromRelations is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param intermediateConceptionKindName in method extractIntermediateConceptionEntitiesFromRelations is required");
            throw e1;
        }
        if(fromRelationKind == null){
            logger.error("Param fromRelationKind in method extractIntermediateConceptionEntitiesFromRelations is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param fromRelationKind in method extractIntermediateConceptionEntitiesFromRelations is required");
            throw e1;
        }
        if(toRelationKind == null){
            logger.error("Param toRelationKind in method extractIntermediateConceptionEntitiesFromRelations is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param toRelationKind in method extractIntermediateConceptionEntitiesFromRelations is required");
            throw e1;
        }
        if(relationEntityUIDs == null || relationEntityUIDs.size() == 0){
            logger.error("At lease one relationEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one relationEntityUID is required");
            throw e1;
        }

        String cypherProcedureString = "MATCH (source)-[rel]->(target)\n" +
                "WHERE id(rel) IN "+relationEntityUIDs.toString()+"\n" +
                "WITH collect(rel) AS rels\n" +
                "CALL apoc.refactor.extractNode(rels,['"+intermediateConceptionKindName+"'],'"+fromRelationKind+"','"+toRelationKind+"')\n" +
                "YIELD output\n" +
                "RETURN output AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(intermediateConceptionKindName,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeWrite(getListConceptionEntityTransformer,cypherProcedureString);
            return conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<RelationEntity> collapseConceptionEntities(List<String> conceptionEntityUIDs, String relationKindName) throws CoreRealmServiceRuntimeException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.collapseNode/
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() == 0){
            logger.error("At lease one conceptionEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one conceptionEntityUID is required");
            throw e1;
        }
        if(relationKindName == null){
            logger.error("Param relationKindName in method collapseConceptionEntities is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param relationKindName in method collapseConceptionEntities is required");
            throw e1;
        }

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "CALL apoc.refactor.collapseNode(targetNodes,'"+relationKindName+"')\n" +
                "YIELD output\n" +
                "RETURN output AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(relationKindName,workingGraphOperationExecutor,false);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (List<RelationEntity>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<RelationEntity> changeEntitiesRelationKind(List<String> relationEntityUIDs, String newRelationKind) throws CoreRealmServiceRuntimeException{
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.setType/
        if(relationEntityUIDs == null || relationEntityUIDs.size() == 0){
            logger.error("At lease one relationEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one relationEntityUID is required");
            throw e1;
        }
        if(newRelationKind == null){
            logger.error("Param newRelationKind in method changeEntitiesRelationKind is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param newRelationKind in method changeEntitiesRelationKind is required");
            throw e1;
        }

        String cypherProcedureString = "MATCH (source)-[rel]->(target)\n" +
                "WHERE id(rel) IN "+relationEntityUIDs.toString()+"\n" +
                "CALL apoc.refactor.setType(rel, '"+newRelationKind+"')\n" +
                "YIELD output\n" +
                "RETURN output AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(newRelationKind,workingGraphOperationExecutor,false);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (List<RelationEntity>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<ConceptionEntity> changeEntitiesConceptionKind(List<String> conceptionEntityUIDs, String oldConceptionKind,String newConceptionKind) throws CoreRealmServiceRuntimeException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() == 0){
            logger.error("At lease one relationEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one relationEntityUID is required");
            throw e1;
        }
        if(oldConceptionKind == null){
            logger.error("Param oldConceptionKind in method changeEntitiesConceptionKind is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param oldConceptionKind in method changeEntitiesConceptionKind is required");
            throw e1;
        }
        if(newConceptionKind == null){
            logger.error("Param newConceptionKind in method changeEntitiesConceptionKind is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param newConceptionKind in method changeEntitiesConceptionKind is required");
            throw e1;
        }

        String cypherProcedureString = "MATCH (targetNodes:"+oldConceptionKind+") WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "SET targetNodes:"+newConceptionKind +"\n"+
                "REMOVE targetNodes:"+oldConceptionKind +"\n"+
                "RETURN count(targetNodes) AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        String cypherProcedureString2 = "MATCH (targetNodes:"+newConceptionKind+") WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "RETURN targetNodes AS operationResult;";

        //Another implement option:
        // https://neo4j.com/docs/apoc/current/overview/apoc.create/apoc.create.addLabels/
        // https://neo4j.com/docs/apoc/current/overview/apoc.create/apoc.create.removeLabels/

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    return null;
                }
            }, cypherProcedureString);

            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(newConceptionKind,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString2);
            return conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<RelationEntity> invertRelationEntitiesDirection(List<String> relationEntityUIDs) throws CoreRealmServiceRuntimeException {
        https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.invert/
        if(relationEntityUIDs == null || relationEntityUIDs.size() == 0){
            logger.error("At lease one relationEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one relationEntityUID is required");
            throw e1;
        }

        String cypherProcedureString = "MATCH (source)-[rel]->(target)\n" +
                "WHERE id(rel) IN "+relationEntityUIDs.toString()+"\n" +
                "CALL apoc.refactor.invert(rel)\n"+
                "YIELD output\n" +
                "RETURN output AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,workingGraphOperationExecutor,false);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (List<RelationEntity>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<RelationEntity> redirectRelationsToNewConceptionEntity(List<String> relationEntityUIDs, String targetConceptionEntityUID, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.from/
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.to/
        if(relationEntityUIDs == null || relationEntityUIDs.size() == 0){
            logger.error("At lease one relationEntityUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one relationEntityUID is required");
            throw e1;
        }
        if(targetConceptionEntityUID == null){
            logger.error("Param targetConceptionEntityUID in method redirectRelationsToNewConceptionEntity is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param targetConceptionEntityUID in method redirectRelationsToNewConceptionEntity is required");
            throw e1;
        }
        if(relationDirection == null){
            logger.error("Param relationDirection in method redirectRelationsToNewConceptionEntity is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param relationDirection in method redirectRelationsToNewConceptionEntity is required");
            throw e1;
        }
        if(relationDirection.equals(RelationDirection.TWO_WAY)){
            logger.error("Param relationDirection in method redirectRelationsToNewConceptionEntity can't be TWO_WAY direction");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param relationDirection in method redirectRelationsToNewConceptionEntity can't be TWO_WAY direction");
            throw e1;
        }

        String redirectLogicCQL="";
        switch(relationDirection) {
            case FROM ->
                    redirectLogicCQL = "CALL apoc.refactor.from(rel, target)";
            case TO ->
                    redirectLogicCQL = "CALL apoc.refactor.to(rel, target)";
        }
        String cypherProcedureString = "MATCH ()-[rel]->()\n" +
                "WHERE id(rel) IN "+relationEntityUIDs.toString()+"\n" +
                "MATCH (target)\n" +
                "WHERE id(target) = "+targetConceptionEntityUID+"\n" +
                redirectLogicCQL+"\n" +
                "YIELD output\n" +
                "RETURN output AS operationResult;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,workingGraphOperationExecutor,false);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListRelationEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (List<RelationEntity>)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public ConceptionEntity mergeConceptionEntities(String remainsConceptionEntityUID, List<String> mergedConceptionEntitiesUIDs) throws CoreRealmServiceRuntimeException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.mergeNodes/
        if(remainsConceptionEntityUID == null){
            logger.error("Param remainsConceptionEntityUID in method mergeConceptionEntities is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param remainsConceptionEntityUID in method mergeConceptionEntities is required");
            throw e1;
        }
        if(mergedConceptionEntitiesUIDs == null || mergedConceptionEntitiesUIDs.size() == 0){
            logger.error("At lease one mergedConceptionEntitiesUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one mergedConceptionEntitiesUID is required");
            throw e1;
        }

        ArrayList allConceptionEntitiesUIDList = new ArrayList();
        allConceptionEntitiesUIDList.add(remainsConceptionEntityUID);
        allConceptionEntitiesUIDList.addAll(mergedConceptionEntitiesUIDs);

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + allConceptionEntitiesUIDList.toString()+"\n"+
                "WITH collect(targetNodes) as nodes\n"+
                "CALL apoc.refactor.mergeNodes(nodes,{\n" +
                "  properties:\"combine\",\n" +
                "  mergeRels:true\n" +
                "})"+
                "YIELD node\n" +
                "RETURN node as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer = new GetSingleConceptionEntityTransformer(null,workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (ConceptionEntity)queryRes;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public RelationEntity mergeRelationEntities(String remainsRelationEntityUID, List<String> mergedRelationEntitiesUIDs) throws CoreRealmServiceRuntimeException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.refactor/apoc.refactor.mergeRelationships/
        if(remainsRelationEntityUID == null){
            logger.error("Param remainsRelationEntityUID in method mergeConceptionEntities is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("Param remainsRelationEntityUID in method mergeConceptionEntities is required");
            throw e1;
        }
        if(mergedRelationEntitiesUIDs == null || mergedRelationEntitiesUIDs.size() == 0){
            logger.error("At lease one mergedRelationEntitiesUID is required");
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage("At lease one mergedRelationEntitiesUID is required");
            throw e1;
        }

        ArrayList allRelationEntitiesUIDList = new ArrayList();
        allRelationEntitiesUIDList.add(remainsRelationEntityUID);
        allRelationEntitiesUIDList.addAll(mergedRelationEntitiesUIDs);

        String cypherProcedureString = "MATCH ()-[targetRel]->() WHERE id(targetRel) IN " + allRelationEntitiesUIDList.toString()+"\n"+
                "WITH collect(targetRel) as rels\n"+
                "CALL apoc.refactor.mergeRelationships(rels,{\n" +
                "  properties:\"combine\"\n" +
                "})"+
                "YIELD rel\n" +
                "RETURN rel as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer(null,workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,cypherProcedureString);
            if(queryRes != null){
                return (RelationEntity)queryRes;
            }
        }catch (org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<RelationEntity> createBiDirectionRelationsByConceptionEntityUIDs(List<String> conceptionEntityUIDs, String relationKind, Map<String, Object> relationAttributes,boolean repeatable) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.isEmpty()){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }
        List<RelationEntity> resultRelationEntityList = new ArrayList<>();

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "RETURN DISTINCT targetNodes as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            List<ConceptionEntity> realConceptionEntityList = conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;

            if(realConceptionEntityList != null){
                for(ConceptionEntity currentConceptionEntity:realConceptionEntityList){
                    List<String> targetConceptionEntityUIDList = new ArrayList<>();
                    for(String currentUID:conceptionEntityUIDs){
                        if(!currentConceptionEntity.getConceptionEntityUID().equals(currentUID)){
                            targetConceptionEntityUIDList.add(currentUID);
                        }
                    }
                    List<RelationEntity> currentResult = currentConceptionEntity.attachToRelation(targetConceptionEntityUIDList,relationKind,relationAttributes,repeatable);
                    if(currentResult != null){
                        resultRelationEntityList.addAll(currentResult);
                    }
                }
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return resultRelationEntityList;
    }

    @Override
    public Set<PathEntitiesSequence> getPathEntitiesSequences(PathEntitiesSequenceMatchPattern sequenceMatchPattern) throws CoreRealmServiceRuntimeException {
        return Set.of();
    }

    @Override
    public Map<String, List<ConceptionKind>> getClassificationsDirectRelatedConceptionKinds(Set<String> classificationNames, String relationKindName, RelationDirection relationDirection){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String classificationsFilter = "";
            if (classificationNames != null && !classificationNames.isEmpty()) {
                classificationsFilter = "WHERE classification.name IN " + convertStringSetToCypherValue(classificationNames);
            }
            String relationTypePart = "[r]";
            if (relationKindName != null) {
                relationTypePart = "[r:`" + relationKindName + "`]";
            }
            String relationMatchingPart = "";
            if (relationDirection == null) {
                relationMatchingPart = "-" + relationTypePart + "-";
            } else {
                switch (relationDirection) {
                    case FROM -> relationMatchingPart = "-" + relationTypePart + "->";
                    case TO -> relationMatchingPart = "<-" + relationTypePart + "-";
                    case TWO_WAY -> relationMatchingPart = "-" + relationTypePart + "-";
                }
            }
            String queryCql = "MATCH (classification:"+RealmConstant.ClassificationClass+")" + relationMatchingPart + "(conceptionKind:"+RealmConstant.ConceptionKindClass+") " + classificationsFilter + " RETURN classification,r, conceptionKind LIMIT 1000000000";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            Map<String,List<ConceptionKind>> resultMap = new HashMap<>();

            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Node classificationNode = nodeRecord.get("classification").asNode();
                        Relationship relation = nodeRecord.get("r").asRelationship();
                        Node conceptionKindNode = nodeRecord.get("conceptionKind").asNode();

                        String classificationName = classificationNode.get(RealmConstant._NameProperty).asString();
                        if(!resultMap.containsKey(classificationName)){
                            List<ConceptionKind> conceptionKindList = new ArrayList<>();
                            resultMap.put(classificationName,conceptionKindList);
                        }

                        List<ConceptionKind> currentClassificationList = resultMap.get(classificationName);

                        long conceptionKindNodeUID = conceptionKindNode.id();
                        String coreRealmName = coreRealm.getCoreRealmName();
                        String conceptionKindName = conceptionKindNode.get(RealmConstant._NameProperty).asString();
                        String conceptionKindDesc = null;
                        if(conceptionKindNode.get(RealmConstant._DescProperty) != null){
                            conceptionKindDesc = conceptionKindNode.get(RealmConstant._DescProperty).asString();
                        }
                        String conceptionKindUID = ""+conceptionKindNodeUID;
                        Neo4JConceptionKindImpl neo4JConceptionKindImpl =
                                new Neo4JConceptionKindImpl(coreRealmName,conceptionKindName,conceptionKindDesc,conceptionKindUID);
                        neo4JConceptionKindImpl.setGlobalGraphOperationExecutor(graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                        currentClassificationList.add(neo4JConceptionKindImpl);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
            return resultMap;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public TimeScaleEventAndConceptionEntityPairRetrieveResult getAttachedTimeScaleEventAndConceptionEntityPairs(List<String> conceptionEntityUIDs, QueryParameters queryParameters) {
        try {
            CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl commonTimeScaleDataPairRetrieveResultImpl = new CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl();
            commonTimeScaleDataPairRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,null);
            String conceptionEntitiesUIDQueryPart = "id(conceptionEntity) IN " + conceptionEntityUIDs.toString();
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)","(conceptionEntity)-[:`"+RealmConstant.TimeScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
            if(queryCql.contains("WHERE")){
                queryCql = queryCql.replace("WHERE","WHERE "+conceptionEntitiesUIDQueryPart+" AND");
            }else{
                queryCql = queryCql.replace("RETURN","WHERE "+conceptionEntitiesUIDQueryPart+" RETURN");
            }
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,"RETURN "+CypherBuilder.operationResultName+",conceptionEntity");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListTimeScaleEventAndConceptionEntityPairTransformer getListTimeScaleEventAndConceptionEntityPairTransformer = new GetListTimeScaleEventAndConceptionEntityPairTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleEventAndConceptionEntityPairTransformer,queryCql);
                if(queryRes != null){
                    List<TimeScaleEventAndConceptionEntityPair> res = (List<TimeScaleEventAndConceptionEntityPair>)queryRes;
                    commonTimeScaleDataPairRetrieveResultImpl.addTimeScaleEventAndConceptionEntityPairs(res);
                    commonTimeScaleDataPairRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonTimeScaleDataPairRetrieveResultImpl.finishEntitiesRetrieving();
            return commonTimeScaleDataPairRetrieveResultImpl;

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long countAttachedTimeScaleEvents(List<String> conceptionEntityUIDs, AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
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
        }

        try {
            CommonTimeScaleEventsRetrieveResultImpl commonTimeScaleEventsRetrieveResultImpl = new CommonTimeScaleEventsRetrieveResultImpl();
            commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String conceptionEntitiesUIDQueryPart = "id(conceptionEntity) IN " + conceptionEntityUIDs.toString();
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)","(conceptionEntity)-[:`"+RealmConstant.TimeScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
            if(queryCql.contains("WHERE")){
                queryCql = queryCql.replace("WHERE","WHERE "+conceptionEntitiesUIDQueryPart+" AND");
            }else{
                queryCql = queryCql.replace("RETURN","WHERE "+conceptionEntitiesUIDQueryPart+" RETURN");
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countConceptionEntitiesRes;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GeospatialScaleEventAndConceptionEntityPairRetrieveResult getAttachedGeospatialScaleEventAndConceptionEntityPairs(List<String> conceptionEntityUIDs, QueryParameters queryParameters) {
        try {
            CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl = new CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl();
            commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEventClass,queryParameters,null);
            String conceptionEntitiesUIDQueryPart = "id(conceptionEntity) IN " + conceptionEntityUIDs.toString();
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)","(conceptionEntity)-[:`"+RealmConstant.GeospatialScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)");
            if(queryCql.contains("WHERE")){
                queryCql = queryCql.replace("WHERE","WHERE "+conceptionEntitiesUIDQueryPart+" AND");
            }else{
                queryCql = queryCql.replace("RETURN","WHERE "+conceptionEntitiesUIDQueryPart+" RETURN");
            }
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,"RETURN "+CypherBuilder.operationResultName+",conceptionEntity");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListGeospatialScaleEventAndConceptionEntityPairTransformer getListGeospatialScaleEventAndConceptionEntityPairTransformer = new GetListGeospatialScaleEventAndConceptionEntityPairTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEventAndConceptionEntityPairTransformer,queryCql);
                if(queryRes != null){
                    List<GeospatialScaleEventAndConceptionEntityPair> res = (List<GeospatialScaleEventAndConceptionEntityPair>)queryRes;
                    commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.addGeospatialScaleEventAndConceptionEntityPairs(res);
                    commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.finishEntitiesRetrieving();
            return commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl;
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long countAttachedGeospatialScaleEvents(List<String> conceptionEntityUIDs, AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
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
        }

        try {
            String conceptionEntitiesUIDQueryPart = "id(conceptionEntity) IN " + conceptionEntityUIDs.toString();
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)","(conceptionEntity)-[:`"+RealmConstant.GeospatialScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)");
            if(queryCql.contains("WHERE")){
                queryCql = queryCql.replace("WHERE","WHERE "+conceptionEntitiesUIDQueryPart+" AND");
            }else{
                queryCql = queryCql.replace("RETURN","WHERE "+conceptionEntitiesUIDQueryPart+" RETURN");
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countConceptionEntitiesRes;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EntitiesOperationResult updateSingleValueConceptionEntityAttributesByUIDs(Map<String, Map<String, Object>> conceptionEntityUIDAndAttributesMap) throws CoreRealmServiceEntityExploreException {
        if(conceptionEntityUIDAndAttributesMap != null){
            CommonEntitiesOperationResultImpl entitiesOperationResult= new CommonEntitiesOperationResultImpl();
            List<String> successEntitiesUIDList = new ArrayList<>();
            if(!conceptionEntityUIDAndAttributesMap.isEmpty()){
                int conceptionEntitiesCount = conceptionEntityUIDAndAttributesMap.size();
                int partitionCount = (int)conceptionEntitiesCount/1000;
                if(partitionCount == 0){
                    partitionCount = 1;
                }
                Set<String> conceptionEntitiesUIDSet = conceptionEntityUIDAndAttributesMap.keySet();
                List<String> targetConceptionEntitiesUIDList = new ArrayList<>(conceptionEntitiesUIDSet);
                List<List<String>> partitionedUIDLists = Lists.partition(targetConceptionEntitiesUIDList,partitionCount);
                for(List<String> currentPartitionedUIDList : partitionedUIDLists){
                    updateConceptionEntitiesAttributes(currentPartitionedUIDList,conceptionEntityUIDAndAttributesMap,successEntitiesUIDList);
                }
            }
            entitiesOperationResult.getSuccessEntityUIDs().addAll(successEntitiesUIDList);
            entitiesOperationResult.getOperationStatistics().setSuccessItemsCount(successEntitiesUIDList.size());
            entitiesOperationResult.getOperationStatistics().setFailItemsCount(conceptionEntityUIDAndAttributesMap.size()-successEntitiesUIDList.size());
            entitiesOperationResult.getOperationStatistics().setOperationSummary("updateSingleValueConceptionEntityAttributesByUIDs operation finished");
            entitiesOperationResult.finishEntitiesOperation();
            return entitiesOperationResult;
        }else{
            return null;
        }
    }

    @Override
    public EntitiesOperationResult updateSingleValueRelationEntityAttributesByUIDs(Map<String, Map<String, Object>> relationEntityUIDAndAttributesMap) throws CoreRealmServiceEntityExploreException {
        if(relationEntityUIDAndAttributesMap != null){
            CommonEntitiesOperationResultImpl entitiesOperationResult= new CommonEntitiesOperationResultImpl();
            List<String> successEntitiesUIDList = new ArrayList<>();
            if(!relationEntityUIDAndAttributesMap.isEmpty()){
                int relationEntitiesCount = relationEntityUIDAndAttributesMap.size();
                int partitionCount = (int)relationEntitiesCount/1000;
                if(partitionCount == 0){
                    partitionCount = 1;
                }
                Set<String> relationEntitiesUIDSet = relationEntityUIDAndAttributesMap.keySet();
                List<String> targetRelationEntitiesUIDList = new ArrayList<>(relationEntitiesUIDSet);
                List<List<String>> partitionedUIDLists = Lists.partition(targetRelationEntitiesUIDList,partitionCount);
                for(List<String> currentPartitionedUIDList : partitionedUIDLists){
                    updateRelationEntitiesAttributes(currentPartitionedUIDList,relationEntityUIDAndAttributesMap,successEntitiesUIDList);
                }
            }
            entitiesOperationResult.getSuccessEntityUIDs().addAll(successEntitiesUIDList);
            entitiesOperationResult.getOperationStatistics().setSuccessItemsCount(successEntitiesUIDList.size());
            entitiesOperationResult.getOperationStatistics().setFailItemsCount(relationEntityUIDAndAttributesMap.size()-successEntitiesUIDList.size());
            entitiesOperationResult.getOperationStatistics().setOperationSummary("updateSingleValueRelationEntityAttributesByUIDs operation finished");
            entitiesOperationResult.finishEntitiesOperation();
            return entitiesOperationResult;
        }else{
            return null;
        }
    }

    @Override
    public DynamicContentQueryResult executeAdhocQuery(String adhocQuerySentence) throws CoreRealmServiceEntityExploreException {
        if(adhocQuerySentence == null){
            logger.error("Adhoc Query Sentence is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("Adhoc Query Sentence is required");
            throw e;
        }else{
            Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap = new HashMap<>();
            DynamicContentQueryResult dynamicContentQueryResult = new DynamicContentQueryResult();
            dynamicContentQueryResult.setStartTime(new Date());
            dynamicContentQueryResult.setAdhocQuerySentence(adhocQuerySentence);
            dynamicContentQueryResult.setDynamicContentAttributesValueTypeMap(dynamicContentAttributesValueTypeMap);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String coreRealmName = this.coreRealm != null ? this.coreRealm.getCoreRealmName() : null;
                GetListDynamicContentValueTransformer getListDynamicContentValueTransformer =
                        new GetListDynamicContentValueTransformer(coreRealmName, dynamicContentAttributesValueTypeMap,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object executeResultObj = workingGraphOperationExecutor.executeRead(getListDynamicContentValueTransformer, adhocQuerySentence);
                if(executeResultObj != null){
                    List<Map<String,DynamicContentValue>> dynamicContentValueList = (List<Map<String,DynamicContentValue>>)executeResultObj;
                    dynamicContentQueryResult.setDynamicContentResultValueList(dynamicContentValueList);
                    dynamicContentQueryResult.setDynamicContentValuesCount(dynamicContentValueList.size());
                }
                dynamicContentQueryResult.setFinishTime(new Date());
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return dynamicContentQueryResult;
        }
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    private class MergeEntitiesToConceptionKindThread implements Runnable {
        private List<RelationEntityValue> relationEntityValueList;
        private Map<String,Object> threadReturnDataMap;
        private RelationDirection relationDirection;
        public MergeEntitiesToConceptionKindThread(List<RelationEntityValue> relationEntityValueList,RelationDirection relationDirection,Map<String,Object> threadReturnDataMap){
            this.relationEntityValueList = relationEntityValueList;
            this.relationDirection = relationDirection;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            DataTransformer<Integer> resultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record record = result.next();
                        if(record.containsKey("count(*)")){
                            return record.get("count(*)").asInt();
                        }
                    }
                    return null;
                }
            };
            int successCount = 0;
            List<String> remainEntityUIDList = new ArrayList<>();
            for(RelationEntityValue currentRelationEntityValue : this.relationEntityValueList){
                String fromEntityUID = currentRelationEntityValue.getFromConceptionEntityUID();
                String toEntityUID = currentRelationEntityValue.getToConceptionEntityUID();
                String removedEntityUID = "";
                String remainedEntityUID = "";
                switch(this.relationDirection){
                    case FROM:
                        remainedEntityUID = toEntityUID;
                        removedEntityUID = fromEntityUID;
                        break;
                    case TO:
                        removedEntityUID = toEntityUID;
                        remainedEntityUID = fromEntityUID;
                }

                String cypherProcedureString = "MATCH (nodeA) WHERE id(nodeA)= " + remainedEntityUID+"\n" +
                        "MATCH (nodeB) WHERE id(nodeB)= " + removedEntityUID+"\n" +
                        "WITH head(collect([nodeA,nodeB])) as nodes\n" +
                        "CALL apoc.refactor.mergeNodes(nodes,{\n" +
                        "    properties:\"combine\",\n" +
                        "    mergeRels:true,\n" +
                        "    produceSelfRel:false\n" +
                        "})\n" +
                        "YIELD node\n" +
                        "RETURN count(*)";
                Object queryRes = graphOperationExecutor.executeWrite(resultDataTransformer,cypherProcedureString);
                if(queryRes != null){
                    int resultNo = (Integer)queryRes;
                    successCount = successCount + resultNo;
                    remainEntityUIDList.add(remainedEntityUID);
                }
            }
            threadReturnDataMap.put("SUCCESSCOUNT_"+currentThreadName,successCount);
            threadReturnDataMap.put("SUCCESSUIDS_"+currentThreadName,remainEntityUIDList);
            graphOperationExecutor.close();
        }
    }

    private String convertStringSetToCypherValue(Set<String> stringSet){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Iterator<String> valueItor = stringSet.iterator();
        while(valueItor.hasNext()){
            String value = valueItor.next();
            sb.append("'"+value+"'");
            if(valueItor.hasNext()){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void updateConceptionEntitiesAttributes(List<String> currentPartitionedUIDList,Map<String, Map<String, Object>> conceptionEntityUIDAndAttributesMap,List<String> successEntitiesUIDList){
        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + currentPartitionedUIDList.toString()+"\n"+
                "RETURN DISTINCT targetNodes as operationResult";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            if(conceptionEntityList != null){
                List<ConceptionEntity> resultConceptionEntitiesList = (List<ConceptionEntity>)conceptionEntityList;
                for(ConceptionEntity currentConceptionEntity:resultConceptionEntitiesList){
                    if(conceptionEntityUIDAndAttributesMap != null && conceptionEntityUIDAndAttributesMap.containsKey(currentConceptionEntity.getConceptionEntityUID())){
                        List<String> operationResult =  currentConceptionEntity.addNewOrUpdateAttributes(conceptionEntityUIDAndAttributesMap.get(currentConceptionEntity.getConceptionEntityUID()));
                        if(successEntitiesUIDList!= null && !operationResult.isEmpty()){
                            successEntitiesUIDList.add(currentConceptionEntity.getConceptionEntityUID());
                        }
                    }
                }
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private void updateRelationEntitiesAttributes(List<String> currentPartitionedUIDList,Map<String, Map<String, Object>> relationEntityUIDAndAttributesMap,List<String> successEntitiesUIDList){
        String cypherProcedureString = "MATCH (source)-[r]->(target)\n" +
                "WHERE id(r) IN "+currentPartitionedUIDList.toString()+"\n" +
                "RETURN DISTINCT r as operationResult,source as sourceNode, target as targetNode";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor(),false);
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
            List<RelationEntity> resultRelationEntitiesList = (List<RelationEntity>)relationEntityList;
            for(RelationEntity currentRelationEntity:resultRelationEntitiesList){
                if(relationEntityUIDAndAttributesMap != null && relationEntityUIDAndAttributesMap.containsKey(currentRelationEntity.getRelationEntityUID())){
                    List<String> operationResult =  currentRelationEntity.addNewOrUpdateAttributes(relationEntityUIDAndAttributesMap.get(currentRelationEntity.getRelationEntityUID()));
                    if(successEntitiesUIDList!= null && !operationResult.isEmpty()){
                        successEntitiesUIDList.add(currentRelationEntity.getRelationEntityUID());
                    }
                }
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }
}
