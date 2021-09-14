package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
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
        return null;
    }
}
