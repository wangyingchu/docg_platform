package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface TimeScaleDataPairRetrieveResult {
    public List<TimeScaleDataPair> getTimeScaleDataPairs();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
