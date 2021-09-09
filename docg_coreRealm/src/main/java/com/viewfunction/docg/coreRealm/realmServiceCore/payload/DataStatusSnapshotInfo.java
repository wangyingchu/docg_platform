package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataStatusSnapshotInfo {

    private long snapshotTookTime;
    private long wholeConceptionEntityCount;
    private long wholeRelationEntityCount;
    private int wholeConceptionKindCount;
    private int wholeRelationKindCount;
    private int wholePhysicAttributeNameCount;
    private Map<String,Long> conceptionKindsDataCount;
    private Map<String,Long> relationKindsDataCount;
    private List<RuntimeRelationAndConceptionKindAttachInfo> relationAndConceptionKindAttachInfo;

    public DataStatusSnapshotInfo(long wholeConceptionEntityCount,long wholeRelationEntityCount,int wholeConceptionKindCount,
                                  int wholeRelationKindCount, int wholePhysicAttributeNameCount,Map<String,Long> conceptionKindsDataCount,
                                  Map<String,Long> relationKindsDataCount,List<RuntimeRelationAndConceptionKindAttachInfo> relationAndConceptionKindAttachInfo){
        this.snapshotTookTime = new Date().getTime();

        this.wholeConceptionEntityCount = wholeConceptionEntityCount;
        this.wholeRelationEntityCount = wholeRelationEntityCount;
        this.wholeConceptionKindCount = wholeConceptionKindCount;
        this.wholeRelationKindCount = wholeRelationKindCount;
        this.wholePhysicAttributeNameCount = wholePhysicAttributeNameCount;
        this.conceptionKindsDataCount = conceptionKindsDataCount;
        this.relationKindsDataCount = relationKindsDataCount;
        this.relationAndConceptionKindAttachInfo = relationAndConceptionKindAttachInfo;
    }

    public long getSnapshotTookTime() {
        return snapshotTookTime;
    }

    public long getWholeConceptionEntityCount() {
        return wholeConceptionEntityCount;
    }

    public long getWholeRelationEntityCount() {
        return wholeRelationEntityCount;
    }

    public int getWholeConceptionKindCount() {
        return wholeConceptionKindCount;
    }

    public int getWholeRelationKindCount() {
        return wholeRelationKindCount;
    }

    public int getWholePhysicAttributeNameCount() {
        return wholePhysicAttributeNameCount;
    }

    public Map<String, Long> getConceptionKindsDataCount() {
        return conceptionKindsDataCount;
    }

    public Map<String, Long> getRelationKindsDataCount() {
        return relationKindsDataCount;
    }

    public List<RuntimeRelationAndConceptionKindAttachInfo> getRelationAndConceptionKindAttachInfo() {
        return relationAndConceptionKindAttachInfo;
    }
}
