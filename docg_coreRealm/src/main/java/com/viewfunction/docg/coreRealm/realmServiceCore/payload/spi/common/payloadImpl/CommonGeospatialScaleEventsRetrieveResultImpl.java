package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonGeospatialScaleEventsRetrieveResultImpl implements GeospatialScaleEventsRetrieveResult {

    private List<GeospatialScaleEvent> geospatialScaleEventList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonGeospatialScaleEventsRetrieveResultImpl(){
        this.geospatialScaleEventList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addGeospatialScaleEvents(List<GeospatialScaleEvent> geospatialScaleEventList){
        this.geospatialScaleEventList.addAll(geospatialScaleEventList);
    }

    @Override
    public List<GeospatialScaleEvent> getGeospatialScaleEvents() {
        return this.geospatialScaleEventList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return this.entitiesRetrieveStatistics;
    }
}
