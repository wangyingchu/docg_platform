package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface GeospatialScaleEventAndConceptionEntityPairRetrieveResult {
    public List<GeospatialScaleEventAndConceptionEntityPair> getGeospatialScaleEventAndConceptionEntityPairs();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
