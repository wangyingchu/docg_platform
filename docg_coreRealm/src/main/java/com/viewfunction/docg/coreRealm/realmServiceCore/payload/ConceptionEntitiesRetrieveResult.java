package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;

public interface ConceptionEntitiesRetrieveResult {
    public List<ConceptionEntity> getConceptionEntities();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
