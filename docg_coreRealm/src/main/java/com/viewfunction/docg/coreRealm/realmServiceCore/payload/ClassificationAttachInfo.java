package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;

public class ClassificationAttachInfo {
    private RelationAttachInfo relationAttachInfo;
    private Classification attachedClassification;

    public RelationAttachInfo getRelationAttachInfo() {
        return relationAttachInfo;
    }

    public void setRelationAttachInfo(RelationAttachInfo relationAttachInfo) {
        this.relationAttachInfo = relationAttachInfo;
    }

    public Classification getAttachedClassification() {
        return attachedClassification;
    }

    public void setAttachedClassification(Classification attachedClassification) {
        this.attachedClassification = attachedClassification;
    }
}
