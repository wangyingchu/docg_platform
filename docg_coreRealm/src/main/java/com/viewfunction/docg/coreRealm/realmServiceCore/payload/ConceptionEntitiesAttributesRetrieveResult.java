package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface ConceptionEntitiesAttributesRetrieveResult {
    public List<ConceptionEntityValue> getConceptionEntityValues();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
