package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class ConceptionKindMatchLogic {

    public enum ConceptionKindExistenceRule {NOT_HAVE, ALL_HAVE,END_WITH,TERMINATE_AT}

    private String conceptionKindName;
    private ConceptionKindExistenceRule conceptionKindExistenceRule;

    public ConceptionKindMatchLogic(String conceptionKindName,ConceptionKindExistenceRule conceptionKindExistenceRule){
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindExistenceRule = conceptionKindExistenceRule != null ? conceptionKindExistenceRule:ConceptionKindExistenceRule.ALL_HAVE;
    }

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public ConceptionKindExistenceRule getConceptionKindExistenceRule() {
        return conceptionKindExistenceRule;
    }
}
