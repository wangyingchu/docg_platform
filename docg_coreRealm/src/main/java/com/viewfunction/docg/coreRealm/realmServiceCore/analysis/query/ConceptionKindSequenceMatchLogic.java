package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class ConceptionKindSequenceMatchLogic implements SequenceMatchLogic{

    private String kinkName;
    private AttributesParameters entityAttributesFilterParameter;

    public ConceptionKindSequenceMatchLogic(String kinkName,AttributesParameters entityAttributesFilterParameter){
        this.kinkName = kinkName;
        this.entityAttributesFilterParameter = entityAttributesFilterParameter;
    }

    @Override
    public String getKindName() {
        return this.kinkName;
    }

    @Override
    public AttributesParameters getEntityAttributesFilterParameter() {
        return this.entityAttributesFilterParameter;
    }
}
