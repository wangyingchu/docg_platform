package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RelationKindMatchLogic implements EntityKindMatchLogic{

    private String relationKindName;
    private RelationDirection relationDirection;

    public RelationKindMatchLogic(String relationKindName,RelationDirection relationDirection){
        this.relationKindName = relationKindName;
        this.relationDirection = relationDirection != null ? relationDirection:RelationDirection.TWO_WAY;
    }

    public String getRelationKindName() {
        return relationKindName;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }
}
