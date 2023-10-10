package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

public class ConceptionKindAttachInfo {
    private RelationAttachInfo relationAttachInfo;
    private ConceptionKind attachedConceptionKind;

    public RelationAttachInfo getRelationAttachInfo() {
        return relationAttachInfo;
    }

    public void setRelationAttachInfo(RelationAttachInfo relationAttachInfo) {
        this.relationAttachInfo = relationAttachInfo;
    }

    public ConceptionKind getAttachedConceptionKind() {
        return attachedConceptionKind;
    }

    public void setAttachedConceptionKind(ConceptionKind attachedConceptionKind) {
        this.attachedConceptionKind = attachedConceptionKind;
    }
}
