package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class GraphDegreeDistributionInfo {

    private String graphName;
    private long p50;
    private long p75;
    private long p90;
    private long p95;
    private long p99;
    private long p999;
    private long max;
    private long min;
    private float mean;

    public GraphDegreeDistributionInfo(String graphName,long p50,long p75,long p90,long p95,long p99,long p999,long max,
                                       long min,float mean ){
        this.graphName = graphName;
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

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public long getP50() {
        return p50;
    }

    public void setP50(long p50) {
        this.p50 = p50;
    }

    public long getP75() {
        return p75;
    }

    public void setP75(long p75) {
        this.p75 = p75;
    }

    public long getP90() {
        return p90;
    }

    public void setP90(long p90) {
        this.p90 = p90;
    }

    public long getP95() {
        return p95;
    }

    public void setP95(long p95) {
        this.p95 = p95;
    }

    public long getP99() {
        return p99;
    }

    public void setP99(long p99) {
        this.p99 = p99;
    }

    public long getP999() {
        return p999;
    }

    public void setP999(long p999) {
        this.p999 = p999;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }
}
