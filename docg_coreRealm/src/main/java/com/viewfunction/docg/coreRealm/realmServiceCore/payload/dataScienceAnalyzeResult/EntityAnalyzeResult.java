package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class EntityAnalyzeResult {

    private String conceptionEntityUID;
    private double value;
    private String valueName = "score";

    public EntityAnalyzeResult(String conceptionEntityUID, double value){
        this.conceptionEntityUID = conceptionEntityUID;
        this.value = value;
    }

    public EntityAnalyzeResult(String conceptionEntityUID, double value, String valueName){
        this.conceptionEntityUID = conceptionEntityUID;
        this.value = value;
        this.valueName = valueName;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public double getValue() {
        return value;
    }

    public String toString(){
        return this.conceptionEntityUID+" -> "+getValueName()+": "+this.value;
    }

    public String getValueName() {
        return valueName;
    }
}
