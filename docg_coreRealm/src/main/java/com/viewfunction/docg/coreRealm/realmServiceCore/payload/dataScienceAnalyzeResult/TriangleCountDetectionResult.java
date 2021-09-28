package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class TriangleCountDetectionResult {

    private String conceptionEntityUID;
    private int triangleCount;

    public TriangleCountDetectionResult(String conceptionEntityUID, int triangleCount){
        this.conceptionEntityUID = conceptionEntityUID;
        this.triangleCount = triangleCount;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public int getTriangleCount() {
        return triangleCount;
    }

    public String toString(){
        return this.conceptionEntityUID+" -> triangleCount: "+this.triangleCount;
    }
}
