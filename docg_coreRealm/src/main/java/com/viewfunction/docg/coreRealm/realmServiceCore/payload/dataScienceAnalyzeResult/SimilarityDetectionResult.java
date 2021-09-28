package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class SimilarityDetectionResult {

    private String conceptionEntityAUID;
    private String conceptionEntityBUID;
    private float similarityScore;

    public SimilarityDetectionResult(String conceptionEntityAUID,String conceptionEntityBUID,float similarityScore){
        this.conceptionEntityAUID = conceptionEntityAUID;
        this.conceptionEntityBUID = conceptionEntityBUID;
        this.similarityScore = similarityScore;
    }

    public String getConceptionEntityAUID() {
        return conceptionEntityAUID;
    }

    public String getConceptionEntityBUID() {
        return conceptionEntityBUID;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }

    public String toString(){
        return this.conceptionEntityAUID+"|"+this.conceptionEntityBUID+" -> similarityScore: "+this.similarityScore;
    }
}
