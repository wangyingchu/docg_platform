package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class ConceptionKindCorrelationInfo {

    private String sourceConceptionKindName;
    private String targetConceptionKindName;
    private String relationKindName;
    private long relationEntityCount;

    public ConceptionKindCorrelationInfo(String sourceConceptionKindName,String targetConceptionKindName,String relationKindName,long relationEntityCount){
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
        this.relationEntityCount = relationEntityCount;
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

    public long getRelationEntityCount(){
        return relationEntityCount;
    }
}
