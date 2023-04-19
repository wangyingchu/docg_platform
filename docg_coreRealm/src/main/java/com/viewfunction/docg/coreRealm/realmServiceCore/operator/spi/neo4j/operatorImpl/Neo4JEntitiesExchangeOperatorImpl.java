package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;

public class Neo4JEntitiesExchangeOperatorImpl implements EntitiesExchangeOperator {

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
