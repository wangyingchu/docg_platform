package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class KindDataDistributionInfo {

    private String[] kindNames;
    private long entitySampleSize;
    private double avgAttributeCount;
    private int minAttributeCount;
    private int maxAttributeCount;
    private int medianAttributeCount;
    private double avgRelationCount;
    private int minRelationCount;
    private int maxRelationCount;
    private int medianRelationCount;

    public KindDataDistributionInfo(String[] kindNames,long entitySampleSize,
                                    double avgAttributeCount,int minAttributeCount,int maxAttributeCount,int medianAttributeCount,
                                    double avgRelationCount,int minRelationCount,int maxRelationCount,int medianRelationCount){
        this.kindNames = kindNames;
        this.entitySampleSize = entitySampleSize;
        this.avgAttributeCount = avgAttributeCount;
        this.minAttributeCount = minAttributeCount;
        this.maxAttributeCount = maxAttributeCount;
        this.medianAttributeCount = medianAttributeCount;
        this.avgRelationCount = avgRelationCount;
        this.minRelationCount = minRelationCount;
        this.maxRelationCount = maxRelationCount;
        this.medianRelationCount = medianRelationCount;
    }

    public String[] getKindNames() {
        return kindNames;
    }

    public long getEntitySampleSize() {
        return entitySampleSize;
    }

    public double getAvgAttributeCount() {
        return avgAttributeCount;
    }

    public int getMinAttributeCount() {
        return minAttributeCount;
    }

    public int getMaxAttributeCount() {
        return maxAttributeCount;
    }

    public int getMedianAttributeCount() {
        return medianAttributeCount;
    }

    public double getAvgRelationCount() {
        return avgRelationCount;
    }

    public int getMinRelationCount() {
        return minRelationCount;
    }

    public int getMaxRelationCount() {
        return maxRelationCount;
    }

    public int getMedianRelationCount() {
        return medianRelationCount;
    }
}
