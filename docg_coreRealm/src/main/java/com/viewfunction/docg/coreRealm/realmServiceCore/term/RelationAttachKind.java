package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;

import java.util.List;
import java.util.Map;

public interface RelationAttachKind {
    public String getRelationAttachKindUID();
    public String getSourceConceptionKindName();
    public String getTargetConceptionKindName();
    public String getRelationKindName();
    public String getRelationAttachKindDesc();
    public boolean updateRelationshipDefDesc(String newDesc);
    public List<RelationAttachLinkLogic> getRelationAttachLinkLogic();
    public RelationAttachLinkLogic addRelationAttachLinkLogic(RelationAttachLinkLogic relationAttachLinkLogic) throws CoreRealmServiceRuntimeException;
    public boolean removeRelationAttachLinkLogic(String relationAttachLinkLogicUID) throws CoreRealmServiceRuntimeException;
    public boolean createRelationEntity(String sourceConceptionKindUID, String targetConceptionKindUID, Map<String,Object> relationData);
    public EntitiesOperationResult createUniversalRelationEntities();
}
