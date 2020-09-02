package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonConceptionEntitiesRetrieveResultImpl implements ConceptionEntitiesRetrieveResult {

    private List<ConceptionEntity> conceptionEntityList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonConceptionEntitiesRetrieveResultImpl(){
        this.conceptionEntityList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addConceptionEntities(List<ConceptionEntity> conceptionEntityList){
        this.conceptionEntityList.addAll(conceptionEntityList);
    }

    @Override
    public List<ConceptionEntity> getConceptionEntities() {
        return this.conceptionEntityList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return this.entitiesRetrieveStatistics;
    }
}
