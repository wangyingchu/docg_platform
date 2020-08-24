package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;

import java.util.List;

public interface Neo4JRelationKindInf extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationKindAttachable {
    public String getRelationKindName();
    public String getRelationKindDesc();
    public Neo4JRelationKindInf getParentRelationKind() throws CoreRealmFunctionNotSupportedException;
    public List<Neo4JRelationKindInf> getChildRelationKinds() throws CoreRealmFunctionNotSupportedException;
    public Long countRelationEntities(boolean includeDescendant);
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters);
}
