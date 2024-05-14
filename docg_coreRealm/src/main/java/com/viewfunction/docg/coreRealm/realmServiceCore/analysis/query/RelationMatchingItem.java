package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RelationMatchingItem {

    private String relatedConceptionKind;
    private String relationKind;
    private RelationDirection relationDirection;
    private boolean isLogicReversed = false;

    public RelationMatchingItem(String relationKind,RelationDirection relationDirection,String relatedConceptionKind){
        this.relationKind = relationKind;
        this.relationDirection = relationDirection;
        this.relatedConceptionKind = relatedConceptionKind;
    }

    public String getRelatedConceptionKind() {
        return relatedConceptionKind;
    }

    public String getRelationKind() {
        return relationKind;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void reversedCondition(){
        this.isLogicReversed = !this.isLogicReversed;
    }

    public boolean isReversedCondition(){
        return isLogicReversed;
    }
}
