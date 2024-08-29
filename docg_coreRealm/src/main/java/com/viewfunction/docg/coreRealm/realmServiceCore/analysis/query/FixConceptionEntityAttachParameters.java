package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class FixConceptionEntityAttachParameters {

    private String conceptionEntityUID;
    private String relationKind;
    private RelationDirection relationDirection;

    public FixConceptionEntityAttachParameters(String conceptionEntityUID,String relationKind,RelationDirection relationDirection) {
        this.conceptionEntityUID = conceptionEntityUID;
        this.relationKind = relationKind;
        this.relationDirection = relationDirection;
    }

    public FixConceptionEntityAttachParameters() {}

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public void setConceptionEntityUID(String conceptionEntityUID) {
        this.conceptionEntityUID = conceptionEntityUID;
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
}
