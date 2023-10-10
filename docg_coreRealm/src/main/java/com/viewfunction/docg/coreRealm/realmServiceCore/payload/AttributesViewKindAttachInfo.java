package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

public class AttributesViewKindAttachInfo {
    private RelationAttachInfo relationAttachInfo;
    private AttributesViewKind attachedAttributesViewKind;

    public RelationAttachInfo getRelationAttachInfo() {
        return relationAttachInfo;
    }

    public void setRelationAttachInfo(RelationAttachInfo relationAttachInfo) {
        this.relationAttachInfo = relationAttachInfo;
    }

    public AttributesViewKind getAttachedAttributesViewKind() {
        return attachedAttributesViewKind;
    }

    public void setAttachedAttributesViewKind(AttributesViewKind attachedAttributesViewKind) {
        this.attachedAttributesViewKind = attachedAttributesViewKind;
    }
}
