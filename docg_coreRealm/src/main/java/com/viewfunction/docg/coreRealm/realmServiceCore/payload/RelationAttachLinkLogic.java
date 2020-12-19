package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;

public class RelationAttachLinkLogic {

    private RelationAttachKind.LinkLogicType linkLogicType;
    private RelationAttachKind.LinkLogicCondition linkLogicCondition;
    private String sourceEntityLinkAttributeName;
    private String targetEntitiesLinkAttributeName;
    private String relationAttachLinkLogicUID;

    public RelationAttachLinkLogic(){}

    public RelationAttachLinkLogic(RelationAttachKind.LinkLogicType linkLogicType, RelationAttachKind.LinkLogicCondition linkLogicCondition,
                                   String sourceEntityLinkAttributeName,String targetEntitiesLinkAttributeName){
        this.linkLogicType = linkLogicType;
        this.linkLogicCondition = linkLogicCondition;
        this.sourceEntityLinkAttributeName = sourceEntityLinkAttributeName;
        this.targetEntitiesLinkAttributeName = targetEntitiesLinkAttributeName;
    }

    public RelationAttachLinkLogic(RelationAttachKind.LinkLogicType linkLogicType, RelationAttachKind.LinkLogicCondition linkLogicCondition,
                                   String sourceEntityLinkAttributeName, String targetEntitiesLinkAttributeName, String relationAttachLinkLogicUID){
        this.linkLogicType = linkLogicType;
        this.linkLogicCondition = linkLogicCondition;
        this.sourceEntityLinkAttributeName = sourceEntityLinkAttributeName;
        this.targetEntitiesLinkAttributeName = targetEntitiesLinkAttributeName;
        this.relationAttachLinkLogicUID = relationAttachLinkLogicUID;
    }

    public RelationAttachKind.LinkLogicType getLinkLogicType() {
        return linkLogicType;
    }

    public void setLinkLogicType(RelationAttachKind.LinkLogicType linkLogicType) {
        this.linkLogicType = linkLogicType;
    }

    public RelationAttachKind.LinkLogicCondition getLinkLogicCondition() {
        return linkLogicCondition;
    }

    public void setLinkLogicCondition(RelationAttachKind.LinkLogicCondition linkLogicCondition) {
        this.linkLogicCondition = linkLogicCondition;
    }

    public String getSourceEntityLinkAttributeName() {
        return sourceEntityLinkAttributeName;
    }

    public void setSourceEntityLinkAttributeName(String sourceEntityLinkAttributeName) {
        this.sourceEntityLinkAttributeName = sourceEntityLinkAttributeName;
    }

    public String getTargetEntitiesLinkAttributeName() {
        return targetEntitiesLinkAttributeName;
    }

    public void setTargetEntitiesLinkAttributeName(String targetEntitiesLinkAttributeName) {
        this.targetEntitiesLinkAttributeName = targetEntitiesLinkAttributeName;
    }

    public String getRelationAttachLinkLogicUID() {
        return relationAttachLinkLogicUID;
    }

    public void setRelationAttachLinkLogicUID(String relationAttachLinkLogicUID) {
        this.relationAttachLinkLogicUID = relationAttachLinkLogicUID;
    }
}
