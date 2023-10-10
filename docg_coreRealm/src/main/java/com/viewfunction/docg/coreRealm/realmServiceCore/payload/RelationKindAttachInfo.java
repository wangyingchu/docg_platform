package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;

public class RelationKindAttachInfo {
    private RelationAttachInfo relationAttachInfo;
    private RelationKind attachedRelationKind;

    public RelationAttachInfo getRelationAttachInfo() {
        return relationAttachInfo;
    }

    public void setRelationAttachInfo(RelationAttachInfo relationAttachInfo) {
        this.relationAttachInfo = relationAttachInfo;
    }

    public RelationKind getAttachedRelationKind() {
        return attachedRelationKind;
    }

    public void setAttachedRelationKind(RelationKind attachedRelationKind) {
        this.attachedRelationKind = attachedRelationKind;
    }
}
