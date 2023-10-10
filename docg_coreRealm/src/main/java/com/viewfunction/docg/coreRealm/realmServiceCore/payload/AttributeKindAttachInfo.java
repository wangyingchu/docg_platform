package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;

public class AttributeKindAttachInfo {
    private RelationAttachInfo relationAttachInfo;
    private AttributeKind attachedAttributeKind;

    public RelationAttachInfo getRelationAttachInfo() {
        return relationAttachInfo;
    }

    public void setRelationAttachInfo(RelationAttachInfo relationAttachInfo) {
        this.relationAttachInfo = relationAttachInfo;
    }

    public AttributeKind getAttachedAttributeKind() {
        return attachedAttributeKind;
    }

    public void setAttachedAttributeKind(AttributeKind attachedAttributeKind) {
        this.attachedAttributeKind = attachedAttributeKind;
    }
}
