package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class ConceptionKindMatchLogic implements EntityKindMatchLogic{

    public enum ConceptionKindExistenceRule {NOT_ALLOW, MUST_HAVE,END_WITH,TERMINATE_AT}

    private String conceptionKindName;
    private ConceptionKindExistenceRule conceptionKindExistenceRule;

    public ConceptionKindMatchLogic(String conceptionKindName,ConceptionKindExistenceRule conceptionKindExistenceRule){
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindExistenceRule = conceptionKindExistenceRule != null ? conceptionKindExistenceRule:ConceptionKindExistenceRule.MUST_HAVE;
    }

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public ConceptionKindExistenceRule getConceptionKindExistenceRule() {
        return conceptionKindExistenceRule;
    }
}
