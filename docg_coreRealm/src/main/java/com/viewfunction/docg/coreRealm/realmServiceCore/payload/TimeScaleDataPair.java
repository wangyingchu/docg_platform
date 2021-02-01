package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

public class TimeScaleDataPair {

    private TimeScaleEvent timeScaleEvent;
    private TimeScaleEntity timeScaleEntity;

    public TimeScaleDataPair(TimeScaleEvent timeScaleEvent,TimeScaleEntity timeScaleEntity){
        this.timeScaleEvent = timeScaleEvent;
        this.timeScaleEntity = timeScaleEntity;
    }

    public TimeScaleEvent getTimeScaleEvent() {
        return timeScaleEvent;
    }

    public TimeScaleEntity getTimeScaleEntity() {
        return this.timeScaleEntity;
    }
}
