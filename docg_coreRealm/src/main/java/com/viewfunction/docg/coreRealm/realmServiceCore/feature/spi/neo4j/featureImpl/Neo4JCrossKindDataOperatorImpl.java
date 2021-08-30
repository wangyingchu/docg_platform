package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListEntitiesPathTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
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
    public List<RelationEntity> getRelationInfoOfConceptionEntityPairs(List<String> conceptionEntityUIDs) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.cover/
        */
        if(conceptionEntityUIDs == null || conceptionEntityUIDs.size() ==0){
            logger.error("At least one conception entity UID is required");
            CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
            e.setCauseMessage("At least one conception entity UID is required");
            throw e;
        }

        String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDs.toString()+"\n"+
                "with collect(targetNodes) as nodes\n" +
                "CALL apoc.algo.cover(nodes)\n" +
                "YIELD rel\n" +
                "RETURN startNode(rel), rel, endNode(rel);";

        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        GetListEntitiesPathTransformer getListEntitiesPathTransformer = new GetListEntitiesPathTransformer(workingGraphOperationExecutor);
        try {

            //Object queryResponse = workingGraphOperationExecutor.executeRead(getListEntitiesPathTransformer,cypherProcedureString);
            //return queryResponse != null? (List<EntitiesPath>)queryResponse : null;


        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }


        return null;
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
