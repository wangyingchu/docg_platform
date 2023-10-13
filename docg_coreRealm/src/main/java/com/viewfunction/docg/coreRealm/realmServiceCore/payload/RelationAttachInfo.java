package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.Map;

public class RelationAttachInfo {
    private String relationKind;
    private RelationDirection relationDirection;
    private Map<String, Object> relationData;
    private String relationEntityUID;

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

    public Map<String, Object> getRelationData() {
        return relationData;
    }

    public void setRelationData(Map<String, Object> relationData) {
        this.relationData = relationData;
    }

    public String getRelationEntityUID() {
        return relationEntityUID;
    }

    public void setRelationEntityUID(String relationEntityUID) {
        this.relationEntityUID = relationEntityUID;
    }
}
