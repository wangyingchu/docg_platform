package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class AttributeValueDistributionInfo {

    private String conceptionKind;
    private String attributeName;
    private long conceptionEntityCount;
    private float p10;
    private float p25;
    private float p40;
    private float p50;
    private float p75;
    private float p90;
    private float p95;
    private float p99;
    private float p999;
    private float max;
    private float min;
    private float minNonZero;
    private float mean;
    private float stDev;

    public AttributeValueDistributionInfo(String conceptionKind,String attributeName,long conceptionEntityCount,float p10,
                                          float p25,float p40,float p50,float p75,float p90,float p95,float p999,float max,
                                          float min,float minNonZero,float mean,float stDev){
        this.conceptionKind = conceptionKind;
        this.attributeName = attributeName;
        this.conceptionEntityCount = conceptionEntityCount;
        this.p10 = p10;
        this.p25 = p25;
        this.p40 = p40;
        this.p50 = p50;
        this.p75 = p75;
        this.p90 = p90;
        this.p95 = p95;
        this.p999 = p999;
        this.max = max;
        this.min = min;
        this.minNonZero = minNonZero;
        this.mean = mean;
        this.stDev = stDev;
    }

    public String getConceptionKind() {
        return conceptionKind;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public long getConceptionEntityCount() {
        return conceptionEntityCount;
    }

    public float getP10() {
        return p10;
    }

    public float getP25() {
        return p25;
    }

    public float getP40() {
        return p40;
    }

    public float getP50() {
        return p50;
    }

    public float getP75() {
        return p75;
    }

    public float getP90() {
        return p90;
    }

    public float getP95() {
        return p95;
    }

    public float getP99() {
        return p99;
    }

    public float getP999() {
        return p999;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public float getMinNonZero() {
        return minNonZero;
    }

    public float getMean() {
        return mean;
    }

    public float getStDev() {
        return stDev;
    }
}
