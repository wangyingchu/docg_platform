package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;

public class RelationAttachLinkLogic {

    private RelationAttachKind.LinkLogicType linkLogicType;
    private RelationAttachKind.LinkLogicCondition linkLogicCondition;
    private String knownEntityLinkAttributeName;
    private String unKnownEntitiesLinkAttributeName;
    private String relationAttachLinkLogicUID;

    public RelationAttachLinkLogic(){}

    public RelationAttachLinkLogic(RelationAttachKind.LinkLogicType linkLogicType, RelationAttachKind.LinkLogicCondition linkLogicCondition,
                                   String knownEntityLinkAttributeName,String unKnownEntitiesLinkAttributeName){
        this.linkLogicType = linkLogicType;
        this.linkLogicCondition = linkLogicCondition;
        this.knownEntityLinkAttributeName = knownEntityLinkAttributeName;
        this.unKnownEntitiesLinkAttributeName = unKnownEntitiesLinkAttributeName;
    }

    public RelationAttachLinkLogic(RelationAttachKind.LinkLogicType linkLogicType, RelationAttachKind.LinkLogicCondition linkLogicCondition,
                                   String knownEntityLinkAttributeName,String unKnownEntitiesLinkAttributeName,String relationAttachLinkLogicUID){
        this.linkLogicType = linkLogicType;
        this.linkLogicCondition = linkLogicCondition;
        this.knownEntityLinkAttributeName = knownEntityLinkAttributeName;
        this.unKnownEntitiesLinkAttributeName = unKnownEntitiesLinkAttributeName;
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

    public String getKnownEntityLinkAttributeName() {
        return knownEntityLinkAttributeName;
    }

    public void setKnownEntityLinkAttributeName(String knownEntityLinkAttributeName) {
        this.knownEntityLinkAttributeName = knownEntityLinkAttributeName;
    }

    public String getUnKnownEntitiesLinkAttributeName() {
        return unKnownEntitiesLinkAttributeName;
    }

    public void setUnKnownEntitiesLinkAttributeName(String unKnownEntitiesLinkAttributeName) {
        this.unKnownEntitiesLinkAttributeName = unKnownEntitiesLinkAttributeName;
    }

    public String getRelationAttachLinkLogicUID() {
        return relationAttachLinkLogicUID;
    }

    public void setRelationAttachLinkLogicUID(String relationAttachLinkLogicUID) {
        this.relationAttachLinkLogicUID = relationAttachLinkLogicUID;
    }
}
