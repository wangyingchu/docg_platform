package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonTimeScaleEventsRetrieveResultImpl implements TimeScaleEventsRetrieveResult {

    private List<TimeScaleEvent> timeScaleEventList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonTimeScaleEventsRetrieveResultImpl(){
        this.timeScaleEventList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addTimeScaleEvents(List<TimeScaleEvent> timeScaleEventList){
        this.timeScaleEventList.addAll(timeScaleEventList);
    }

    @Override
    public List<TimeScaleEvent> getTimeScaleEvents() {
        return this.timeScaleEventList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return this.entitiesRetrieveStatistics;
    }
}
