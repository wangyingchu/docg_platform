package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class RelationMatchingItem {

    private RelationKindMatchLogic relationKindMatchLogic;

    private RelationMatchParameters.MatchingLogic matchingLogic;

    public RelationMatchingItem(RelationKindMatchLogic relationKindMatchLogic,RelationMatchParameters.MatchingLogic matchingLogic){
        this.relationKindMatchLogic = relationKindMatchLogic;
        this.matchingLogic = matchingLogic;
    }

    public RelationKindMatchLogic getRelationKindMatchLogic() {
        return relationKindMatchLogic;
    }

    public void setRelationKindMatchLogic(RelationKindMatchLogic relationKindMatchLogic) {
        this.relationKindMatchLogic = relationKindMatchLogic;
    }

    public RelationMatchParameters.MatchingLogic getMatchingLogic() {
        return matchingLogic;
    }

    public void setMatchingLogic(RelationMatchParameters.MatchingLogic matchingLogic) {
        this.matchingLogic = matchingLogic;
    }

    public void reversedCondition(){}

    public boolean isReversedCondition(){return true;}
}
