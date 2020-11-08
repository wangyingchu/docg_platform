package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonRelationEntitiesRetrieveResultImpl implements RelationEntitiesRetrieveResult {

    private List<RelationEntity> relationEntityList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonRelationEntitiesRetrieveResultImpl(){
        this.relationEntityList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addRelationEntities(List<RelationEntity> relationEntityList){
        this.relationEntityList.addAll(relationEntityList);
    }

    @Override
    public List<RelationEntity> getRelationEntities() {
        return this.relationEntityList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return this.entitiesRetrieveStatistics;
    }
}
