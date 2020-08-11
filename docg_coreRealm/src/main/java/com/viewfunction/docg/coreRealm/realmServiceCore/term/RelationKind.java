package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;

import java.util.List;

public interface RelationKind {
    public String getRelationKindName();
    public String getRelationKindDesc();
    public boolean updateRelationKindDesc(String relationKindDesc);
    public RelationKind getParentRelationKind();
    public List<RelationKind> getChildRelationKinds();
    public Long countRelationEntities(boolean includeDescendant);
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters);
}
