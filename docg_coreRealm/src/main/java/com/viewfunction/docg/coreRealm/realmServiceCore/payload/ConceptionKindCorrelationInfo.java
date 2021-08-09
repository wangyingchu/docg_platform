package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class ConceptionKindCorrelationInfo {

    private String sourceConceptionKindName;
    private String targetConceptionKindName;
    private String relationKindName;

    public ConceptionKindCorrelationInfo(String sourceConceptionKindName,String targetConceptionKindName,String relationKindName){
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
    }

    public String getSourceConceptionKindName() {
        return sourceConceptionKindName;
    }

    public String getTargetConceptionKindName() {
        return targetConceptionKindName;
    }

    public String getRelationKindName() {
        return relationKindName;
    }
}
