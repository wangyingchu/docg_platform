package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;

public interface EntitiesExchangeOperator {

    public EntitiesOperationStatistics importConceptionEntitiesFromArrow(String conceptionKindName, String arrowFileLocation);

    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName,String arrowFileLocation);

    public EntitiesOperationStatistics importConceptionEntitiesFromCSV(String conceptionKindName,String csvFileLocation);

    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName,String csvFileLocation);
}
