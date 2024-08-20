package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class ClassificationAttachParameters {

    private String attachedClassification;
    private String relationKind;
    private RelationDirection relationDirection;
    private boolean allowOffspringAttach = false;

    public ClassificationAttachParameters(){

    }

    public String getAttachedClassification() {
        return attachedClassification;
    }

    public void setAttachedClassification(String attachedClassification) {
        this.attachedClassification = attachedClassification;
    }

    public String getRelationKind() {
        return relationKind;
    }

    public void setRelationKind(String relationKind) {
        this.relationKind = relationKind;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }

    public boolean isAllowOffspringAttach() {
        return allowOffspringAttach;
    }

    public void setAllowOffspringAttach(boolean allowOffspringAttach) {
        this.allowOffspringAttach = allowOffspringAttach;
    }
}
