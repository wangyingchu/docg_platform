package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAnalyzableGraphTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JDataScienceOperatorImpl implements DataScienceOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(DataScienceOperator.class);

    public Neo4JDataScienceOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public List<AnalyzableGraph> getAnalyzableGraphs() {
        return null;
    }

    @Override
    public boolean checkAnalyzableGraphExistence(String graphName) {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-exists
        */
        String cypherProcedureString = "CALL gds.graph.exists('"+graphName+"') YIELD exists;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer<Boolean> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                if(result.hasNext()){
                    Record nodeRecord = result.next();
                    return nodeRecord.get("exists").asBoolean();
                }
                return false;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            return queryResponse != null ? (Boolean)queryResponse : false;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AnalyzableGraph getAnalyzableGraph(String graphName) {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-list
        */
        String cypherProcedureString = "CALL gds.graph.list('"+graphName+"');\n";// +
                //"YIELD graphName, nodeQuery, relationshipQuery, nodeCount, relationshipCount, schema, creationTime, modificationTime, memoryUsage;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GetSingleAnalyzableGraphTransformer getSingleAnalyzableGraphTransformer =
                new GetSingleAnalyzableGraphTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleAnalyzableGraphTransformer,cypherProcedureString);
            return queryResponse != null ? (AnalyzableGraph) queryResponse : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException{
        return false;
    }

    @Override
    public AnalyzableGraph createAnalyzableGraph(String graphName, List<String> conceptionKindList,
                                                 List<String> relationKindList) throws CoreRealmServiceRuntimeException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/native-projection/
        */

        if(conceptionKindList == null || conceptionKindList.size() ==0){
            logger.error("At least one ConceptionKind is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one ConceptionKind is required");
            throw e;
        }

        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(checkGraphExistence){
            logger.error("AnalyzableGraph with name {} already exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" already exist");
            throw e;
        }

        String conceptionKindsString = getKindNamesArrayString(conceptionKindList);
        String relationKindsString = ( relationKindList == null || relationKindList.size() == 0) ? "'*'" :
                getKindNamesArrayString(relationKindList);
        String cypherProcedureString = "CALL gds.graph.create('"+graphName+"', "+conceptionKindsString+", "+relationKindsString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {

                if(result.hasNext()){
                    Record nodeRecord = result.next();

                }
                return null;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            //return relationEntityList != null ? (List<RelationEntity>)relationEntityList : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }


        return null;

    }

    private String getKindNamesArrayString(List<String> kindNamesList){
        String kindNamesArrayString = "[";

        for(int i= 0; i<kindNamesList.size();i++){
            kindNamesArrayString = kindNamesArrayString +"'"+kindNamesList.get(i)+"'";
            if(i<kindNamesList.size()-1){
                kindNamesArrayString = kindNamesArrayString + ",";
            }
        }
        kindNamesArrayString = kindNamesArrayString+"]";
        return kindNamesArrayString;
    }
}
