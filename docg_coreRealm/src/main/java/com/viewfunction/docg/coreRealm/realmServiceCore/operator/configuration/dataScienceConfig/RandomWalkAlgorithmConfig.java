package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class RandomWalkAlgorithmConfig extends DataScienceBaseAlgorithmConfig {

    public enum WalkStrategy {random,node2vec}
    private String sourceConceptionEntityUID;
    private int walkSteps = 10;
    private int walksNumber = 1;
    private WalkStrategy walkStrategy = WalkStrategy.random;
    private float node2vecInOut = 1.0f;
    private float node2vecReturn = 1.0f;

    public String getSourceConceptionEntityUID() {
        return sourceConceptionEntityUID;
    }

    public void setSourceConceptionEntityUID(String sourceConceptionEntityUID) {
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
    }

    public int getWalkSteps() {
        return walkSteps;
    }

    public void setWalkSteps(int walkSteps) {
        this.walkSteps = walkSteps;
    }

    public int getWalksNumber() {
        return walksNumber;
    }

    public void setWalksNumber(int walksNumber) {
        this.walksNumber = walksNumber;
    }

    public WalkStrategy getWalkStrategy() {
        return walkStrategy;
    }

    public void setWalkStrategy(WalkStrategy walkStrategy) {
        this.walkStrategy = walkStrategy;
    }

    public float getNode2vecInOut() {
        return node2vecInOut;
    }

    public void setNode2vecInOut(float node2vecInOut) {
        this.node2vecInOut = node2vecInOut;
    }

    public float getNode2vecReturn() {
        return node2vecReturn;
    }

    public void setNode2vecReturn(float node2vecReturn) {
        this.node2vecReturn = node2vecReturn;
    }
}
