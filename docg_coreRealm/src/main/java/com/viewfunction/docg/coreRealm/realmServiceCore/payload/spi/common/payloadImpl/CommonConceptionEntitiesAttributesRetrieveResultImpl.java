package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonConceptionEntitiesAttributesRetrieveResultImpl implements ConceptionEntitiesAttributesRetrieveResult {

    private List<ConceptionEntityValue> conceptionEntityValueList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonConceptionEntitiesAttributesRetrieveResultImpl(){
        this.conceptionEntityValueList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addConceptionEntitiesAttributes(List<ConceptionEntityValue> conceptionEntityValueList){
        this.conceptionEntityValueList.addAll(conceptionEntityValueList);
    }

    @Override
    public List<ConceptionEntityValue> getConceptionEntityValues() {
        return conceptionEntityValueList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return entitiesRetrieveStatistics;
    }
}
