package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RelationMatchingItem {

    private String relatedConceptionKind;
    private String relationKind;
    private RelationDirection relationDirection;
    private boolean isLogicReversed = false;
    private Integer relationJump;

    public RelationMatchingItem(String relationKind,RelationDirection relationDirection,String relatedConceptionKind){
        this.relationKind = relationKind;
        this.relationDirection = relationDirection;
        this.relatedConceptionKind = relatedConceptionKind;
        this.relationJump = 1;
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

    public Integer getRelationJump() {
        return relationJump;
    }

    public void setRelationJump(Integer relationJump) {
        this.relationJump = relationJump;
    }
}
