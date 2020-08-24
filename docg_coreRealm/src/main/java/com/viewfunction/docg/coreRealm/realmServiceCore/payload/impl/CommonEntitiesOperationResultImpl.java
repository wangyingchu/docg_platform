package com.viewfunction.docg.coreRealm.realmServiceCore.payload.impl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonEntitiesOperationResultImpl implements EntitiesOperationResult {

    private List<String> successEntityUIDs;
    private EntitiesOperationStatistics entitiesOperationStatistics;

    public CommonEntitiesOperationResultImpl(){
        this.successEntityUIDs = new ArrayList<>();
        this.entitiesOperationStatistics = new EntitiesOperationStatistics();
        this.entitiesOperationStatistics.setStartTime(new Date());
    }

    public void finishEntitiesOperation(){
        this.entitiesOperationStatistics.setFinishTime(new Date());
    }

    @Override
    public List<String> getSuccessEntityUIDs(){
        return successEntityUIDs;
    }

    @Override
    public EntitiesOperationStatistics getOperationStatistics() {
        return entitiesOperationStatistics;
    }
}
