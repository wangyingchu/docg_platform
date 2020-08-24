package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;

import java.util.List;
import java.util.Map;

public interface RelationAttachKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationKindAttachable {
    public String getRelationAttachKindUID();
    public String getSourceConceptionKindName();
    public String getTargetConceptionKindName();
    public String getRelationKindName();
    public String getRelationAttachKindDesc();
    public boolean updateRelationAttachKindDesc(String newDesc);
    public List<RelationAttachLinkLogic> getRelationAttachLinkLogic();
    public RelationAttachLinkLogic createRelationAttachLinkLogic(RelationAttachLinkLogic relationAttachLinkLogic) throws CoreRealmServiceRuntimeException;
    public boolean removeRelationAttachLinkLogic(String relationAttachLinkLogicUID) throws CoreRealmServiceRuntimeException;
    public boolean newRelationEntity(String sourceConceptionKindUID, String targetConceptionKindUID, Map<String,Object> relationData);
    public EntitiesOperationResult newUniversalRelationEntities();
}
