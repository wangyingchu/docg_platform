package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

import java.util.List;

public interface TimeScaleEventsRetrieveResult {
    public List<TimeScaleEvent> getTimeScaleEvents();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
