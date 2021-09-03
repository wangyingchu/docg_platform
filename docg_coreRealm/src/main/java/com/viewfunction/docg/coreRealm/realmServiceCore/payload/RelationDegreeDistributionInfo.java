package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

public class RelationDegreeDistributionInfo {

    private String relationKind;
    private RelationDirection relationDirection;
    private long relationEntityCount;
    private long p50;
    private long p75;
    private long p90;
    private long p95;
    private long p99;
    private long p999;
    private long max;
    private long min;
    private float mean;

    public RelationDegreeDistributionInfo(String relationKind,RelationDirection relationDirection,long relationEntityCount
            ,long p50,long p75,long p90,long p95,long p99,long p999,long max,long min,float mean){
        this.relationKind = relationKind;
        this.relationDirection = relationDirection;
        this.relationEntityCount = relationEntityCount;
        this.p50 = p50;
        this.p75 = p75;
        this.p90 = p90;
        this.p95 = p95;
        this.p99 = p99;
        this.p999 = p999;
        this.max = max;
        this.min = min;
        this.mean = mean;
    }

    public String getRelationKind() {
        return relationKind;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public long getRelationEntityCount() {
        return relationEntityCount;
    }

    public long getP50() {
        return p50;
    }

    public long getP75() {
        return p75;
    }

    public long getP90() {
        return p90;
    }

    public long getP95() {
        return p95;
    }

    public long getP99() {
        return p99;
    }

    public long getP999() {
        return p999;
    }

    public long getMax() {
        return max;
    }

    public long getMin() {
        return min;
    }

    public float getMean() {
        return mean;
    }
}
