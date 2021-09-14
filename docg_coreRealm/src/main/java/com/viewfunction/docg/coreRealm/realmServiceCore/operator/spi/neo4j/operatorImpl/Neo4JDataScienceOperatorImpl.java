package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
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
        return false;
    }

    @Override
    public AnalyzableGraph getAnalyzableGraph(String graphName) {
        return null;
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

        String conceptionKindsString = getKindNamesArrayString(conceptionKindList);

        String relationKindsString = ( relationKindList == null || relationKindList.size() == 0) ? "'*'" :
                getKindNamesArrayString(relationKindList);
        String cypherProcedureString = "CALL gds.graph.create('"+graphName+"', "+conceptionKindsString+", "+relationKindsString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);






        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListRelationEntityTransformer getListRelationEntityTransformer = new GetListRelationEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationEntityList = workingGraphOperationExecutor.executeRead(getListRelationEntityTransformer,cypherProcedureString);
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
