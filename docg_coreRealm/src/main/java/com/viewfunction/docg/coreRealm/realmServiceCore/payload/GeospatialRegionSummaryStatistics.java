package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class GeospatialRegionSummaryStatistics {

    private long containsTotalGeospatialScaleEntityCount;
    private long refersTotalGeospatialScaleEventCount;

    public long getContainsTotalGeospatialScaleEntityCount() {
        return containsTotalGeospatialScaleEntityCount;
    }

    public void setContainsTotalGeospatialScaleEntityCount(long containsTotalGeospatialScaleEntityCount) {
        this.containsTotalGeospatialScaleEntityCount = containsTotalGeospatialScaleEntityCount;
    }

    public long getRefersTotalGeospatialScaleEventCount() {
        return refersTotalGeospatialScaleEventCount;
    }

    public void setRefersTotalGeospatialScaleEventCount(long refersTotalGeospatialScaleEventCount) {
        this.refersTotalGeospatialScaleEventCount = refersTotalGeospatialScaleEventCount;
    }
}
