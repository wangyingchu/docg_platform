package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface TimeScaleEventAndConceptionEntityPairRetrieveResult {
    public List<TimeScaleEventAndConceptionEntityPair> getTimeScaleEventAndConceptionEntityPairs();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
