package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventAndConceptionEntityPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventAndConceptionEntityPairRetrieveResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl implements TimeScaleEventAndConceptionEntityPairRetrieveResult {

    private List<TimeScaleEventAndConceptionEntityPair> timeScaleEventAndConceptionEntityPairList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl(){
        this.timeScaleEventAndConceptionEntityPairList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addTimeScaleDataPairs(List<TimeScaleEventAndConceptionEntityPair> timeScaleEventAndConceptionEntityPairList){
        this.timeScaleEventAndConceptionEntityPairList.addAll(timeScaleEventAndConceptionEntityPairList);
    }

    @Override
    public List<TimeScaleEventAndConceptionEntityPair> getTimeScaleEventAndConceptionEntityPairs() {
        return this.timeScaleEventAndConceptionEntityPairList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return this.entitiesRetrieveStatistics;
    }
}
