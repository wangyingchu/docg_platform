package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;

import java.util.List;

public interface RelationKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    public String getRelationKindName();
    public String getRelationKindDesc();
    public RelationKind getParentRelationKind() throws CoreRealmFunctionNotSupportedException;
    public List<RelationKind> getChildRelationKinds() throws CoreRealmFunctionNotSupportedException;
    public InheritanceTree<RelationKind> getOffspringRelationKinds() throws CoreRealmFunctionNotSupportedException;
    public Long countRelationEntities(boolean includeDescendant);
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters);
    public EntitiesOperationResult purgeAllRelationEntities() throws CoreRealmServiceRuntimeException;
}
