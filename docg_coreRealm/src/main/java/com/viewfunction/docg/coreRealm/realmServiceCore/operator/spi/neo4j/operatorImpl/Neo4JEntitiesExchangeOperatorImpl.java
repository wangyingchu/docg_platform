package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4JEntitiesExchangeOperatorImpl implements EntitiesExchangeOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(DataScienceOperator.class);

    public Neo4JEntitiesExchangeOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public EntitiesOperationStatistics importConceptionEntitiesFromArrow(String conceptionKindName, String arrowFileLocation) {
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName, String arrowFileLocation) {
        return null;
    }

    @Override
    public EntitiesOperationStatistics importConceptionEntitiesFromCSV(String conceptionKindName, String csvFileLocation) {
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName, String csvFileLocation) {
        return null;
    }
}
