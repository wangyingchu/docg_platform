package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class DistanceDetectionResult {

    private String sourceConceptionEntityUID;
    private String targetConceptionEntityUID;
    private double distance;

    public DistanceDetectionResult(String sourceConceptionEntityUID,String targetConceptionEntityUID,double distance){
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
        this.targetConceptionEntityUID = targetConceptionEntityUID;
        this.distance = distance;
    }

    public String getSourceConceptionEntityUID() {
        return sourceConceptionEntityUID;
    }

    public String getTargetConceptionEntityUID() {
        return targetConceptionEntityUID;
    }

    public double getDistance() {
        return distance;
    }

    public String toString(){
        return this.sourceConceptionEntityUID+"|"+this.targetConceptionEntityUID+" -> distance: "+this.distance;
    }
}
