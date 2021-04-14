package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.List;

public interface RelationEntitiesAttributesRetrieveResult {
    public List<RelationEntityValue> getRelationEntityValues();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
