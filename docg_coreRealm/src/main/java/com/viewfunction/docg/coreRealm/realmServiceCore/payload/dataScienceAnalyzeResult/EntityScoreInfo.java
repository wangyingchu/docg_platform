package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class EntityScoreInfo {

    private String conceptionEntityUID;
    private double score;

    public EntityScoreInfo(String conceptionEntityUID, double score){
        this.conceptionEntityUID = conceptionEntityUID;
        this.score = score;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public double getScore() {
        return score;
    }

    public String toString(){
        return this.conceptionEntityUID+" -> "+this.score;
    }
}
