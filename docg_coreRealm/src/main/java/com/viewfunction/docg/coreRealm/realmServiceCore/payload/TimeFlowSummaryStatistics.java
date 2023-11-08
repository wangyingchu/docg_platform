package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class TimeFlowSummaryStatistics {

    private long containsTotalTimeScaleEntityCount;
    private long refersTotalTimeScaleEventCount;

    public long getContainsTotalTimeScaleEntityCount() {
        return containsTotalTimeScaleEntityCount;
    }

    public void setContainsTotalTimeScaleEntityCount(long containsTotalTimeScaleEntityCount) {
        this.containsTotalTimeScaleEntityCount = containsTotalTimeScaleEntityCount;
    }

    public long getRefersTotalTimeScaleEventCount() {
        return refersTotalTimeScaleEventCount;
    }

    public void setRefersTotalTimeScaleEventCount(long refersTotalTimeScaleEventCount) {
        this.refersTotalTimeScaleEventCount = refersTotalTimeScaleEventCount;
    }
}
