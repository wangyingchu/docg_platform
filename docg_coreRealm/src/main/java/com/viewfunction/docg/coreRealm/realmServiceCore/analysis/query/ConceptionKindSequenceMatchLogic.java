package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.List;

public class ConceptionKindSequenceMatchLogic implements SequenceMatchLogic{

    private String kindName;
    private AttributesParameters entityAttributesFilterParameter;
    private List<String> returnAttributeNames;

    public ConceptionKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
    }

    public ConceptionKindSequenceMatchLogic(String kindName, AttributesParameters entityAttributesFilterParameter,List<String> returnAttributeNames){
        this.kindName = kindName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
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

    @Override
    public List<String> getReturnAttributeNames(){
        return this.returnAttributeNames;
    }

    public void setReturnAttributeNames(List<String> returnAttributeNames){
        this.returnAttributeNames = returnAttributeNames;
    }
}
