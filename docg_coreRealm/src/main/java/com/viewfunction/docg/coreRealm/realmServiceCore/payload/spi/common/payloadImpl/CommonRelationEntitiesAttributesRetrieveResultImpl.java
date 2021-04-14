package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonRelationEntitiesAttributesRetrieveResultImpl implements RelationEntitiesAttributesRetrieveResult {

    private List<RelationEntityValue> relationEntityValueList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonRelationEntitiesAttributesRetrieveResultImpl(){
        this.relationEntityValueList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addRelationEntitiesAttributes(List<RelationEntityValue> relationEntityValueList){
        this.relationEntityValueList.addAll(relationEntityValueList);
    }

    @Override
    public List<RelationEntityValue> getRelationEntityValues() {
        return relationEntityValueList;
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return entitiesRetrieveStatistics;
    }
}
