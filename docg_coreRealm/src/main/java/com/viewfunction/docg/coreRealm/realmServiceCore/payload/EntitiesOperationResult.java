package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface EntitiesOperationResult {
    public List<String> getSuccessEntityUIDs();
    public EntitiesOperationStatistics getOperationStatistics();
}
