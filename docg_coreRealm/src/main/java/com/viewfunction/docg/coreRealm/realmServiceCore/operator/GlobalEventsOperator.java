package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

public interface GlobalEventsOperator {

    public boolean fuseEvents(TimeScaleEvent timeScaleEvent, GeospatialScaleEvent geospatialScaleEvent,String fuseComment);

    public boolean joinEventToConceptionKinds(TimeScaleEvent timeScaleEvent,String[] newKindNames);

    public boolean retreatEventFromConceptionKind(TimeScaleEvent timeScaleEvent,String kindName);

    public boolean joinEventToConceptionKinds(GeospatialScaleEvent geospatialScaleEvent,String[] newKindNames);

    public boolean retreatEventFromConceptionKind(GeospatialScaleEvent geospatialScaleEvent,String kindName);
}
