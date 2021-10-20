package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JCrossKindDataOperatorImpl implements CrossKindDataOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(CrossKindDataOperator.class);

    public Neo4JCrossKindDataOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
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
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            return relationEntityList != null ? (List<ConceptionEntity>)relationEntityList : null;
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

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
