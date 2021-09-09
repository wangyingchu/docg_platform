package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RuntimeRelationAndConceptionKindAttachInfo {

    private String conceptionKind;
    private String relationKind;
    private RelationDirection relationDirection;
    private long relationEntityCount;

    public RuntimeRelationAndConceptionKindAttachInfo(String relationKind,String conceptionKind,
                                                      RelationDirection relationDirection,long relationEntityCount){
        this.relationKind = relationKind;
        this.conceptionKind = conceptionKind;
        this.relationDirection = relationDirection;
        this.relationEntityCount = relationEntityCount;
    }

    public String getConceptionKind() {
        return conceptionKind;
    }

    public String getRelationKind() {
        return relationKind;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public long getRelationEntityCount() {
        return relationEntityCount;
    }
}
