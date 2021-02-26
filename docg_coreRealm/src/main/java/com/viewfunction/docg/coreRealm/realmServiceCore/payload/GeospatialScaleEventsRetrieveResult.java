package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;

import java.util.List;

public interface GeospatialScaleEventsRetrieveResult {
    public List<GeospatialScaleEvent> getGeospatialScaleEvents();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
