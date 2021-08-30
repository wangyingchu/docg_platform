package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
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

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
