package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

public class TimeScaleEventAndConceptionEntityPair {

    private TimeScaleEvent timeScaleEvent;
    private ConceptionEntity conceptionEntity;

    public TimeScaleEventAndConceptionEntityPair(TimeScaleEvent timeScaleEvent, ConceptionEntity conceptionEntity){
        this.timeScaleEvent = timeScaleEvent;
        this.conceptionEntity = conceptionEntity;
    }

    public TimeScaleEvent getTimeScaleEvent() {
        return timeScaleEvent;
    }

    public ConceptionEntity getConceptionEntity() {
        return conceptionEntity;
    }
}
