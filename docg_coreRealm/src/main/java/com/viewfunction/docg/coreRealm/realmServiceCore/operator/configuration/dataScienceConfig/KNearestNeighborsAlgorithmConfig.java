package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class KNearestNeighborsAlgorithmConfig extends ResultPaginationableConfig {

    private String nodeWeightAttribute;
    private int topK = 10;
    private float sampleRate = 0.5f;
    private float deltaThreshold = 0.001f;
    private int maxIterations = 100;
    private int randomJoins = 10;
    private int randomSeed = -1;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public KNearestNeighborsAlgorithmConfig(String nodeWeightAttribute){
        this.nodeWeightAttribute = nodeWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getSimilaritySortingLogic() {
        return valueSortingLogic;
    }

    public void setSimilaritySortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public float getDeltaThreshold() {
        return deltaThreshold;
    }

    public void setDeltaThreshold(float deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getRandomJoins() {
        return randomJoins;
    }

    public void setRandomJoins(int randomJoins) {
        this.randomJoins = randomJoins;
    }

    public int getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    public String getNodeWeightAttribute() {
        return nodeWeightAttribute;
    }
}
