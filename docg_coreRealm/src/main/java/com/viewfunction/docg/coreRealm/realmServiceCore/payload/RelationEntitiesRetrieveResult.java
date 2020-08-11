package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface RelationEntitiesRetrieveResult {
    public List<RelationEntity> getRelationEntities();
    public EntitiesRetrieveStatistics getOperationStatistics();
}
