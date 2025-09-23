package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RelationKindSequenceMatchLogic implements SequenceMatchLogic{

    private String kinkName;
    private AttributesParameters entityAttributesFilterParameter;
    private RelationDirection relationDirection;

    public RelationKindSequenceMatchLogic(String kinkName,AttributesParameters entityAttributesFilterParameter){
        this.kinkName = kinkName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = RelationDirection.TWO_WAY;
    }

    public RelationKindSequenceMatchLogic(String kinkName,AttributesParameters entityAttributesFilterParameter,RelationDirection relationDirection){
        this.kinkName = kinkName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = relationDirection;
    }

    @Override
    public String getKindName() {
        return this.kinkName;
    }

    @Override
    public AttributesParameters getEntityAttributesFilterParameter() {
        return this.entityAttributesFilterParameter;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }
}
