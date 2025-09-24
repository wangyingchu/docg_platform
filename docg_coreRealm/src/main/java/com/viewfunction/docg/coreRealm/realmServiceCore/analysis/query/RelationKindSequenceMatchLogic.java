package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;

public class RelationKindSequenceMatchLogic implements SequenceMatchLogic{

    private String kindName;
    private AttributesParameters entityAttributesFilterParameter;
    private RelationDirection relationDirection;
    private List<String> returnAttributeNames;

    public RelationKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = RelationDirection.TWO_WAY;
    }

    public RelationKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter, List<String> returnAttributeNames){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = RelationDirection.TWO_WAY;
        this.returnAttributeNames = returnAttributeNames;
    }

    public RelationKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter, RelationDirection relationDirection){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = relationDirection;
    }

    public RelationKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter, RelationDirection relationDirection, List<String> returnAttributeNames){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
        this.relationDirection = relationDirection;
        this.returnAttributeNames = returnAttributeNames;
    }

    @Override
    public String getKindName() {
        return this.kindName;
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

    @Override
    public List<String> getReturnAttributeNames(){
        return this.returnAttributeNames;
    }

    public void setReturnAttributeNames(List<String> returnAttributeNames){
        this.returnAttributeNames = returnAttributeNames;
    }
}
