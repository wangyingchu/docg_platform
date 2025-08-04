package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.time.ZonedDateTime;

public class ConceptionKindCorrelationInfo {

    private String sourceConceptionKindName;
    private String targetConceptionKindName;
    private String relationKindName;
    private long relationEntityCount;
    private ZonedDateTime createDate;

    public ConceptionKindCorrelationInfo(String sourceConceptionKindName,String targetConceptionKindName,String relationKindName,long relationEntityCount){
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
        this.relationEntityCount = relationEntityCount;
    }

    public ConceptionKindCorrelationInfo(String sourceConceptionKindName,String targetConceptionKindName,String relationKindName,long relationEntityCount,ZonedDateTime createDate){
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
        this.relationEntityCount = relationEntityCount;
        this.createDate = createDate;
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

    public ZonedDateTime getCreateDate(){
        return createDate;
    }
}
